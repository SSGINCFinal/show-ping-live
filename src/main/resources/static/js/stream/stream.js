var ws = new WebSocket('ws://' + location.host + '/live');
var rec = new WebSocket('ws://' + location.host + '/record');
var live;
var watch;
var webRtcPeer;
var webRtcRecord;
var state;

let stompClient = null;
let memberId = null;
let memberRole = null;
let reconnectTimeout = 5000;

const NO_CALL = 0;
const IN_CALL = 1;
const POST_CALL = 2;
const DISABLED = 3;
const IN_PLAY = 4;

window.onload = function() {
    live = document.getElementById('live-video');
    watch = document.getElementById('live');

    getMemberInfo();

    // send 버튼 이벤트와 STOMP 연결 초기화
    const sendButton = document.getElementById('send-button');
    if (sendButton) {
        sendButton.addEventListener('click', sendChatMessage);
    }

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
                // 신고 대상 채팅 내용 (reportTargetText)
                const reportContent = document.getElementById('reportTargetText').textContent;
                axios.post('/report/api/register', {
                    reportReason: reasonValue,
                    reportContent: reportContent
                })
                    .then(response => {
                        console.log("신고 등록 완료:", response.data);
                        alert("신고가 접수되었습니다.");
                        closeReportModal();
                    })
                    .catch(error => {
                        console.error("신고 등록 중 오류 발생:", error);
                        alert("신고 등록 중 오류가 발생했습니다.");
                    });
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

    // 시청모드이면 접속시 viewer 함수 실행
    if (watch) {
        viewer();
    }

    // streamInfo == null이면(등록된 방송 정보가 없다면) 방송 시작, 방송 종료 버튼 비활성화
    if (!streamInfo) {
        setState(DISABLED);
    } else {    // 기존에 등록된 방송 정보가 있다면 방송 시작 버튼 활성화, 방송 종료 버튼 비활성화
        setState(NO_CALL);
    }
}

window.onbeforeunload = function() {
    ws.close();
    rec.close();
}

function setState(nextState) {
    switch (nextState) {
        case NO_CALL:
            $('#start').attr('disabled', false);
            $('#stop').attr('disabled', true);
            break;
        case DISABLED:
            $('#start').attr('disabled', true);
            $('#stop').attr('disabled', true);
            break;
        case IN_CALL:
            $('#start').attr('disabled', true);
            $('#stop').attr('disabled', false);
            break;
        case POST_CALL:
            $('#start').attr('disabled', false);
            $('#stop').attr('disabled', true);
            break;
        case IN_PLAY:
            $('#start').attr('disabled', true);
            $('#stop').attr('disabled', false);
            break;
        default:
            onLiveError('Unknown state ' + nextState);
            return;
    }
    state = nextState;
}

ws.onmessage = function(message) {
    let parsedMessage = JSON.parse(message.data);
    console.info('Received message: ' + message.data);

    switch (parsedMessage.id) {
        case 'presenterResponse':
            presenterResponse(parsedMessage);
            break;
        case 'viewerResponse':
            viewerResponse(parsedMessage);
            break;
        case 'iceCandidate':
            webRtcPeer.addIceCandidate(parsedMessage.candidate, function(error) {
                if (error)
                    return console.error('Error adding candidate: ' + error);
            });
            break;
        case 'stopCommunication':
            dispose();
            break;
        default:
            console.error('Unrecognized message', parsedMessage);
    }
}

rec.onmessage = function(message) {
    let parsedMessage = JSON.parse(message.data);
    console.info('Received message: ' + message.data);

    switch (parsedMessage.id) {
        case 'startResponse':
            startResponse(parsedMessage);
            break;
        case 'stop':
            dispose();
            break;
        case 'iceCandidate':
            webRtcRecord.addIceCandidate(parsedMessage.candidate, function(error) {
                if (error)
                    return console.error('Error adding candidate: ' + error);
            });
            break;
        case 'recording':
            break;
        case 'stopped':
            // uploadFileToNCP();
            stopChat();
            break;
        default:
            console.error('Unrecognized message', parsedMessage);
    }
}

function presenterResponse(message) {
    setState(IN_CALL);
    console.log('SDP answer received from server. Processing ...');

    webRtcPeer.processAnswer(message.sdpAnswer, function(error) {
        if (error) {
            reject(error);
        }
        createVideo();
    })
}

function startResponse(message) {
    console.log('SDP answer received from server. Processing ...');
    webRtcRecord.processAnswer(message.sdpAnswer, function(error) {
        if (error)
            return console.error(error);
    });
    createChatRoom();
}

function viewerResponse(message) {
    if (message.response != 'accepted') {
        var errorMsg = message.message ? message.message : 'Unknown error';
        console.info('Call not accepted for the following reason: ' + errorMsg);
    } else {
        webRtcPeer.processAnswer(message.sdpAnswer, function(error) {
            if (error)
                return console.error(error);
        });
    }
    connectToChatRoom();
}

function startLive() {
    axios.post("/stream/start", {
        streamNo: streamNo
    })
        .then((response) => {
            console.log(response);

            const data = response.data;

            const productElement = document.querySelector(".product-element");
            document.getElementById("broadcastTitle").value = data.streamTitle;
            document.getElementById("broadcastDesc").value = data.streamDescription;

            productElement.innerHTML = `
                <img src="/img/product_img/${data.productImg}" alt="상품 이미지" class="product-img">
                <div class="product-info" id="${data.productNo}">
                    <p class="product-name">${data.productName}</p>
                    <div class="product-price-container">
                        <p class="product-origin-price">${data.productPrice}</p>
                    </div>
                </div>
            `

            document.getElementById("discountRate").value = data.productSale;

            if (!webRtcPeer) {
                showSpinner(live);

                let options = {
                    localVideo: live,
                    onicecandidate: onLiveIceCandidate
                }
                webRtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(options,
                    function (error) {
                        if (error) {
                            return console.error(error);
                        }
                        webRtcPeer.generateOffer(onLiveOffer);
                    });
            }
        })
        .catch((error) => {
            console.log(error);
        });
}

function viewer() {
    if (!webRtcPeer) {
        showSpinner(watch);

        var options = {
            remoteVideo : watch,
            onicecandidate : onLiveIceCandidate
        }
        webRtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(options,
            function(error) {
                if (error) {
                    return console.error(error);
                }
                this.generateOffer(onViewOffer);
            });
    }
}

function onLiveIceCandidate(candidate) {
    console.log("Local candidate" + JSON.stringify(candidate));

    let message = {
        id : 'onIceCandidate',
        candidate : candidate
    };
    sendLiveMessage(message);
}

function createVideo() {
    console.log('Starting video call ...');

    let options = {
        localVideo : live,
        mediaConstraints : getConstraints(),
        onicecandidate : onRecordIceCandidate
    }

    webRtcRecord = new kurentoUtils.WebRtcPeer.WebRtcPeerSendrecv(options,
                function(error) {
            if (error)
                return console.error(error);
            webRtcRecord.generateOffer(onRecordOffer);
        });
}

function onRecordIceCandidate(candidate) {
    console.log("Local candidate" + JSON.stringify(candidate));

    let message = {
        id : 'onIceCandidate',
        candidate : candidate
    };
    sendRecordMessage(message);
}

function onLiveOffer(error, offerSdp) {
    if (error)
        return console.error('Error generating the offer');
    console.info('Invoking SDP offer callback function ' + location.host);
    let message = {
        id : 'presenter',
        sdpOffer : offerSdp
    }
    sendLiveMessage(message);
}

function onViewOffer(error, offerSdp) {
    if (error)
        return console.error('Error generating the offer');
    console.info('Invoking SDP offer callback function ' + location.host);
    var message = {
        id : 'viewer',
        sdpOffer : offerSdp
    }
    sendLiveMessage(message);
}

function onRecordOffer(error, offerSdp) {
    if (error)
        return console.error('Error generating the offer');
    console.info('Invoking SDP offer callback function ' + location.host);
    let message = {
        id : 'start',
        sdpOffer : offerSdp,
        mode :  $('input[name="mode"]:checked').val()
    }
    sendRecordMessage(message);
}

function getConstraints() {
    return {
        audio: true,
        video: true
    };
}

function stopLive() {
    axios.post("/stream/stop", {
        streamNo: streamNo
    })
        .then((response) => {
            console.log(response);

            let message = {
                id: 'stop'
            }
            if (live) {
                dispose();
            }
            sendLiveMessage(message);
            stopRecord();
        })
        .catch((error) => {
            console.log(error);
        });
}

function stopRecord() {
    let stopMessageId = (state === IN_CALL) ? 'stop' : 'stopPlay';
    console.log('Stopping video while in ' + state + '...');
    if (webRtcRecord) {
        webRtcRecord.dispose();
        webRtcRecord = null;
        setState(NO_CALL);

        let message = {
            id : stopMessageId
        }
        sendRecordMessage(message);
    }

    hideSpinner(live);
}

function dispose() {
    if (webRtcPeer) {
        webRtcPeer.dispose();
        webRtcPeer = null;
    }
    stopChat();
}

function sendLiveMessage(message) {
    let jsonMessage = JSON.stringify(message);
    console.log('Sending message: ' + jsonMessage);
    ws.send(jsonMessage);
}

function sendRecordMessage(message) {
    let jsonMessage = JSON.stringify(message);
    console.log('Sending message: ' + jsonMessage);
    rec.send(jsonMessage);
}

function showSpinner() {
    for (let i = 0; i < arguments.length; i++) {
        arguments[i].poster = '/img/transparent-1px.png';
        arguments[i].style.background = 'center transparent url("./img/spinner.gif") no-repeat';
    }
}

function hideSpinner() {
    for (let i = 0; i < arguments.length; i++) {
        arguments[i].src = '';
        arguments[i].poster = '/img/webrtc.png';
        arguments[i].style.background = '';
    }
}

function onLiveError(error) {
    console.error(error);
}

function uploadFileToNCP() {
    let title = 'test.mp4';
    axios.post('/stream/vod/upload', {
        title
    })
        .then(response => {
            console.log("응답 데이터: ", response.data)
        })
        .catch(error => {
            console.log("에러 발생: ", error);
        });
    stopChat();
}

function createChatRoom() {
    // API 호출
    axios.post(`/chatRoom/create`, {
        streamNo
    })
        .then(response => {
            console.log(response.data);
            alert(`채팅방 번호: ${response.data.chatRoomNo}`);
            chatRoomNo = response.data.chatRoomNo;
            connectToChatRoom();
        })
        .catch(error => {
            console.error('채팅방 생성 중 오류 발생:', error);
            alert('채팅방 생성 중 오류가 발생했습니다.');
        });
}

function connectToChatRoom() {
    // 이미 stompClient가 연결되어 있으면 재연결하지 않음
    if (stompClient && stompClient.connected) {
        console.log('이미 채팅방에 연결되어 있습니다.');
        return;
    }

    const socket = new SockJS('/ws-stomp-chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onChatError);
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

    if (memberRole && memberRole === "ROLE_ADMIN") {
        userNameSpan.textContent = "관리자 ✓";
        // 관리자 이름을 빨간색으로 표시
        userNameSpan.style.color = "red";
        messageElement.classList.add("admin");
        messageTextP.style.color = "red";
        messageTextP.textContent = message.chat_message;
    } else {
        userNameSpan.textContent = message.chat_member_id;
        messageTextP.textContent = message.chat_message;

        // 아이디 클릭 시 신고 모달 오픈
        userNameSpan.addEventListener('click', () => {
            openReportModal(message.chat_member_id, message.chat_message);
        });
    }

    // 요소 조합
    messageElement.appendChild(userNameSpan);
    messageElement.appendChild(messageTextP);

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

// 연결 에러 콜백
function onChatError(error) {
    console.error('STOMP error:', error);

    // 일정 시간 후 재연결 시도
    setTimeout(function () {
        console.log('Attempting to reconnect...');
        connectToChatRoom();
    }, reconnectTimeout);
}


function stopChat() {
    if (stompClient) {
        stompClient.disconnect(() => {
            console.log('Disconnected from WebSocket');
        });
    }
}

function clearErrorMessage() {
    const errorElement = document.getElementById('error-message');
    if (errorElement) {
        errorElement.textContent = '';
        errorElement.style.display = 'none';
    }
}

function getMemberInfo(){
    // accessToken을 sessionStorage에서 가져옴
    const accessToken = sessionStorage.getItem('accessToken');
    if (accessToken) {
        // [추가!!!!] 사용자 정보를 가져오는 API 호출, 응답에서 memberId를 설정
        axios.get('/chat/api/info', {
            headers: {
                Authorization: 'Bearer ' + accessToken
            }
        })
            .then(response => {
                memberId = response.data.memberId; // API에서 memberId를 반환
                memberRole = response.data.role;
                console.log("[DEBUG] Retrieved memberId:", memberId);
                console.log("[DEBUG] Retrieved memberRole:", memberRole);
            })
            .catch(error => {
                console.error("사용자 정보를 불러오는 중 오류:", error);
            });
    } else {
        console.warn("accessToken이 존재하지 않습니다.");
    }
}


/**
 * Lightbox utility (to display media pipeline image in a modal dialog)
 */
$(document).delegate('*[data-toggle="lightbox"]', 'click', function(event) {
    event.preventDefault();
    $(this).ekkoLightbox();
});
