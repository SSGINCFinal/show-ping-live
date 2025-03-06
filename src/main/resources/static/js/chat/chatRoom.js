let stompClient = null;
let reconnectTimeout = 5000;

window.onload = function() {

    // 쿠키에서 accessToken 추출
    const accessToken = getCookieValue("accessToken");
    console.log("AccessToken:", accessToken);

// JWT 페이로드 디코딩
    const payload = parseJwt(accessToken);
    console.log("JWT Payload:", payload);

// 만약 JWT 페이로드에 memberId나 sub 필드가 있다면:
    const extractedMemberId = payload ? payload.sub || payload.memberId : null;
    console.log("추출된 MemberId:", extractedMemberId);

    console.log("chatRoomNo =", chatRoomNo); // Thymeleaf 치환 결과 확인
    console.log("memberId =", memberId);

    const chat = document.getElementById('chat-container');
    document.getElementById('send-button').addEventListener('click', sendChatMessage);
    connectToChatRoom();  // STOMP 연결 초기화
};

function connectToChatRoom() {
    const socket = new SockJS('/ws-stomp-chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}

// 연결 성공 콜백
function onConnected(frame) {
    console.log('Connected to WebSocket:', frame);

    // 채팅방 구독
    stompClient.subscribe(`/sub/chat/room/${chatRoomNo}`, function (message) {
        console.log("Received message from subscription:", message);
        try {
            const parsedMessage = JSON.parse(message.body);
            console.log("Parsed message:", parsedMessage);
            addMessageToChat(parsedMessage);
        } catch (e) {
            console.error("Error parsing message:", e);
        }
    });
}

// 연결 에러 콜백
function onError(error) {
    console.error('STOMP error:', error);

    // 일정 시간 후 재연결 시도
    setTimeout(function() {
        console.log('Attempting to reconnect...');
        connectToChatRoom();
    }, reconnectTimeout);
}

function createChatRoom() {
    const chatContainer = document.getElementById('chat-container');
    const scrollToLatestButton = document.getElementById('scroll-to-latest');

    chatContainer.addEventListener('scroll', function () {
        if (chatContainer.scrollTop + chatContainer.clientHeight < chatContainer.scrollHeight) {
            scrollToLatestButton.style.display = 'block';
        } else {
            scrollToLatestButton.style.display = 'none';
        }
    });

    scrollToLatestButton.addEventListener('click', function () {
        chatContainer.scrollTop = chatContainer.scrollHeight;
        scrollToLatestButton.style.display = 'none';
    });


}

function stopChat() {
    if (stompClient) {
        stompClient.disconnect(() => {
            console.log('Disconnected from WebSocket');
        });
    }
}

// 채팅 메시지 송신 함수
function sendChatMessage() {
    const input = document.getElementById('message-input');
    const messageText = input.value.trim();
    console.log("Attempting to send message:", messageText);

    if (!stompClient || !stompClient.connected) {
        console.warn('WebSocket이 연결되어 있지 않습니다. 재연결 시도 후 다시 전송해 주세요.');
        return;
    }

    if (messageText) {
        const message = {
            _id: "", // 서버 또는 DB에서 할당
            chat_member_id: memberId,
            chat_room_no : chatRoomNo,
            chat_message: messageText,
            chat_created_at: new Date().toLocaleString()
        };
        console.log("Sending message:", message);
        stompClient.send('/pub/chat/message', {}, JSON.stringify(message));
        input.value = ''; // 입력 필드 초기화
    }
}

function parseJwt(token) {
    if (!token) return null;
    const base64Url = token.split('.')[1];
    if (!base64Url) return null;
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    try {
        return JSON.parse(decodeURIComponent(escape(window.atob(base64))));
    } catch (e) {
        console.error('JWT 파싱 실패:', e);
        return null;
    }
}


function getCookieValue(cookieName) {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === cookieName) {
            return decodeURIComponent(value);
        }
    }
    return null;
}

function addMessageToChat(message) {
    // 채팅 메시지를 삽입할 요소 선택 (chat-messages div)
    const chatMessagesContainer = document.getElementById('chat-messages');

    // 새 메시지 요소 생성
    const messageElement = document.createElement("div");
    messageElement.classList.add("message");


    // 사용자 아이디
    const userNameSpan = document.createElement("span");
    // userNameSpan.classList.add("user-name");
    // userNameSpan.textContent = message.chat_member_id;

    // 메시지 텍스트
    const messageTextP = document.createElement("p");
    messageTextP.classList.add("chat-text");
    messageTextP.textContent = message.chat_message;

    // 관리자인지 확인 (필요에 따라 조건 수정)
    if(message.chat_member_id === "ADMIN") {
        messageElement.classList.add("ADMIN");
        userNameSpan.textContent = "관리자 ✓";
        // 관리자 이름을 빨간색으로 표시
        userNameSpan.style.color = "red";

        // 관리자 메시지라고 구분할 클래스 추가 (CSS 활용 가능)
        messageElement.classList.add("admin");

        // 예시: “관리자가 작성한 채팅 입니다.”로 고정
        messageTextP.textContent = message.chat_message;
    } else {
        messageElement.classList.add("USER");
        userNameSpan.textContent = message.chat_member_id;
        messageTextP.textContent = message.chat_message;
    }

    // 채팅 시간 (문자열 그대로 사용하거나, 포맷팅 가능)
    // const chatTimeSpan = document.createElement("span");
    // chatTimeSpan.classList.add("chat-time");
    // chatTimeSpan.textContent = message.chat_created_at;

    // 요소 조합
    messageElement.appendChild(userNameSpan);
    messageElement.appendChild(messageTextP);
    // messageElement.appendChild(chatTimeSpan);

    // 채팅 메시지 컨테이너에 추가
    chatMessagesContainer.appendChild(messageElement);

    // 자동 스크롤
    setTimeout(() => {
        chatMessagesContainer.scrollTop = chatMessagesContainer.scrollHeight;
    }, 100);
}