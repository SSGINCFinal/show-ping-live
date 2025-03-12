let stompClient = null;
let reconnectTimeout = 5000;

window.onload = function () {

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

};

function connectToChatRoom() {
    const socket = new SockJS('/ws-stomp-chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}

// 연결 성공 콜백
function onConnected(frame) {
    console.log('Connected to WebSocket:', frame);
    console.log(document.getElementById('error-message'));


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

    // STOMP 클라이언트 연결 후 에러 채널 구독
    stompClient.subscribe('/user/queue/errors', function(message) {
        var errorResponse = JSON.parse(message.body);
        alert(errorResponse.chatMessage);
    });

}

// 연결 에러 콜백
function onError(error) {
    console.error('STOMP error:', error);

    // 일정 시간 후 재연결 시도
    setTimeout(function () {
        console.log('Attempting to reconnect...');
        connectToChatRoom();
    }, reconnectTimeout);
}

function createChatRoom() {
    let streamNo = document.getElementById('streamNoInput') ? document.getElementById('streamNoInput').value : '3'; // 기본값 예: 1

    // API 호출
    fetch(`/chatRoom/create?streamNo=${encodeURIComponent(streamNo)}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('채팅방 생성 실패');
            }
            return response.json();
        })
        .then(data => {
            console.log('생성된 채팅방:', data);
            alert(`채팅방 번호: ${data.chatRoomNo}`);
        })
        .catch(error => {
            console.error('채팅방 생성 중 오류 발생:', error);
            alert('채팅방 생성 중 오류가 발생했습니다.');
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
    const charCount = document.getElementById('char-count');
    console.log("Attempting to send message:", messageText);

    if (!stompClient || !stompClient.connected) {
        console.warn('WebSocket이 연결되어 있지 않습니다. 재연결 시도 후 다시 전송해 주세요.');
        return;
    }
    // 메세지 전송 전 기존 에러 메세지 초기화
    clearErrorMessage();

    if (messageText) {
        const message = {
            _id: "", // 서버 또는 DB에서 할당
            chat_member_id: memberId,
            chat_room_no: chatRoomNo,
            chat_message: messageText,
            chat_created_at: new Date().toLocaleString()
        };
        console.log("Sending message:", message);
        stompClient.send('/pub/chat/message', {}, JSON.stringify(message));
        input.value = ''; // 입력 필드 초기화
        charCount.textContent = '0/200'; // 글자 수 초기화
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
    userNameSpan.classList.add("user-name");
    userNameSpan.textContent = message.chat_member_id;

    // 메시지 텍스트
    const messageTextP = document.createElement("p");
    messageTextP.classList.add("chat-text");
    messageTextP.textContent = message.chat_message;

    // // 관리자인지 확인 (필요에 따라 조건 수정)
    // if(message.chat_member_id === "ADMIN") {
    //     messageElement.classList.add("ADMIN");
    //     userNameSpan.textContent = "관리자 ✓";
    //     // 관리자 이름을 빨간색으로 표시
    //     userNameSpan.style.color = "red";
    //
    //     // 관리자 메시지라고 구분할 클래스 추가 (CSS 활용 가능)
    //     messageElement.classList.add("admin");
    //
    //     // 예시: “관리자가 작성한 채팅 입니다.”로 고정
    //     messageTextP.textContent = message.chat_message;
    // } else {
    //     messageElement.classList.add("USER");
    //     userNameSpan.textContent = message.chat_member_id;
    //     messageTextP.textContent = message.chat_message;
    // }

    if (message.chat_member_id === "admin01") {
        userNameSpan.textContent = "관리자 ✓";
        // 관리자 이름을 빨간색으로 표시
        userNameSpan.style.color = "red";
        messageElement.classList.add("admin");
        messageTextP.style.color = "red";
        messageTextP.textContent = message.chat_message;
    } else {
        userNameSpan.textContent = message.chat_member_id;
        messageTextP.textContent = message.chat_message;

        // (추가) 아이디 클릭 시 신고 모달 오픈
        userNameSpan.addEventListener('click', () => {
            openReportModal(message.chat_member_id, message.chat_message);
        });
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



function openReportModal(memberId, chatMessage) {
    // 신고 대상 정보 세팅
    const reportTargetText = document.getElementById('reportTargetText');
    reportTargetText.textContent = `${memberId}님: ${chatMessage}`;

    // 모달 & 오버레이 표시
    const reportModal = document.getElementById('reportModal');
    const modalOverlay = document.getElementById('modalOverlay');

    reportModal.style.display = 'block';
    modalOverlay.style.display = 'block';
}

document.addEventListener('DOMContentLoaded', () => {
    // --- 초기화: 쿠키, JWT, 변수, STOMP 연결 ---
    const accessToken = getCookieValue("accessToken");
    console.log("AccessToken:", accessToken);

    const payload = parseJwt(accessToken);
    console.log("JWT Payload:", payload);

    const extractedMemberId = payload ? payload.sub || payload.memberId : null;
    console.log("추출된 MemberId:", extractedMemberId);

    console.log("chatRoomNo =", chatRoomNo); // Thymeleaf 치환 결과 확인
    console.log("memberId =", memberId);

    // send 버튼 이벤트와 STOMP 연결 초기화
    const sendButton = document.getElementById('send-button');
    if (sendButton) {
        sendButton.addEventListener('click', sendChatMessage);
    }
    connectToChatRoom();

    // --- 신고 모달 관련 이벤트 ---
    const reportForm = document.getElementById('reportForm');
    const cancelBtn = document.getElementById('cancelBtn');
    const reportModal = document.getElementById('reportModal');
    const modalOverlay = document.getElementById('modalOverlay');

    if (reportForm) {
        reportForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const checkedReason = document.querySelector('input[name="reportReason"]:checked');
            if (checkedReason) {
                const reasonValue = checkedReason.value;
                alert(`신고 사유: ${reasonValue}`);
                closeReportModal();
            }
        });
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
            alert('신고를 취소했습니다.');
            closeReportModal();
        });
    }

    if (modalOverlay) {
        modalOverlay.addEventListener('click', () => {
            closeReportModal();
        });
    }

    function closeReportModal() {
        if (reportModal) {
            reportModal.style.display = 'none';
        }
        if (modalOverlay) {
            modalOverlay.style.display = 'none';
        }
    }

    // --- 채팅 영역 관련 이벤트 ---
    const messageInput = document.getElementById('message-input');
    const chatContainer = document.getElementById('chat-messages');
    const scrollToLatestButton = document.getElementById('scroll-to-latest');
    const charCount = document.getElementById('char-count');

    if (chatContainer && scrollToLatestButton) {
        chatContainer.addEventListener('scroll', function() {
            if (chatContainer.scrollTop + chatContainer.clientHeight < chatContainer.scrollHeight - 20) {
                scrollToLatestButton.style.display = 'block';
            } else {
                scrollToLatestButton.style.display = 'none';
            }
        });

        scrollToLatestButton.addEventListener('click', function() {
            chatContainer.scrollTop = chatContainer.scrollHeight;
            scrollToLatestButton.style.display = 'none';
        });
    }

    if (messageInput) {
        // Enter 키로 메시지 전송
        messageInput.addEventListener('keypress', (event) => {
            if (event.key === 'Enter') {
                event.preventDefault();
                sendChatMessage();
            }
        });

        // 글자 수 업데이트
        messageInput.addEventListener('input', () => {
            const length = messageInput.value.length;
            if (charCount) {
                charCount.textContent = `${length}/200`;
            }
        });
    }
});


function showErrorMessage(errorText) {
    const errorElement = document.getElementById('error-message');
    if (errorElement) {
        errorElement.textContent = errorText;
        errorElement.style.display = 'block';
    } else {
        console.error("에러 메시지 표시 요소를 찾을 수 없습니다.");
    }
}

function clearErrorMessage() {
    const errorElement = document.getElementById('error-message');
    if (errorElement) {
        errorElement.textContent = '';
        errorElement.style.display = 'none';
    }
}
