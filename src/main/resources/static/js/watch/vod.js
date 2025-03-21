// ================= 기존 변수 선언 =================
const videoElement = document.getElementById('vod'); // id 'vod'와 일치
const track = videoElement.addTextTrack("subtitles", "Korean", "ko");
let chatMessages = [];
let nextChatIndex = 0;

// ================= 기존 코드를 약간 수정 =================
// 전역 변수 window.streamStartTime은 이미 HTML inline 스크립트에서 설정됨
window.streamStartTime = new Date(window.streamStartTime);  // (확실함)

// ================= 수정된 parseChatCreatedAt 함수 =================
// 채팅 시간 문자열(예: "2025-03-21 03:28:43.242800")를 ISO 8601 형식("2025-03-21T03:28:43.242")으로 변환하여 Date 객체로 반환
// function parseChatCreatedAt(timestampStr) {
//     console.log("[DEBUG] 원본 timestampStr:", timestampStr);
//     // 변환 로직
//     let fixedStr = timestampStr.replace(/(\.\d{3})\d+/, '$1');
//     fixedStr = fixedStr.replace(" ", "T");
//     console.log("[DEBUG] 변환 후 fixedStr:", fixedStr);
//
//     let parsedDate = new Date(fixedStr);
//     console.log("[DEBUG] parsedDate:", parsedDate);
//
//     if (isNaN(parsedDate.getTime())) {
//         console.error("[ERROR] Date 파싱 실패:", fixedStr);
//     }
//     return parsedDate;
// }

// 수동 파싱
function parseChatCreatedAt(timestampStr) {
    console.log("[DEBUG] 원본 timestampStr:", timestampStr);
    // 수동 파싱 방식: "2025-03-21 03:28:43.242800" → "2025-03-21T03:28:43.242"
    const [datePart, timePart] = timestampStr.split(' ');
    if (!datePart || !timePart) return null;
    const [year, month, day] = datePart.split('-').map(Number);
    let [hms, msecStr] = timePart.split('.');
    if (!msecStr) msecStr = '000';
    msecStr = msecStr.slice(0, 3);
    const [hour, minute, second] = hms.split(':').map(Number);
    const parsedDate = new Date(year, month - 1, day, hour, minute, second, parseInt(msecStr, 10));
    console.log("[DEBUG] parsedDate:", parsedDate);
    return parsedDate;
}

// ================= 추가된 getOffsetSeconds 함수 =================
// 채팅 발생 시간과 스트림 시작 시간의 차이를 초 단위로 계산 (확실)
function getOffsetSeconds(chatTimeStr) {
    const chatDate = parseChatCreatedAt(chatTimeStr);
    if (!chatDate) {
        console.warn(`잘못된 채팅 시간 데이터로 인해 메시지를 건너뜀: ${chatTimeStr}`);
        return NaN;
    }
    const offsetMs = chatDate.getTime() - window.streamStartTime.getTime();
    return offsetMs / 1000;
}

// ================= 수정된 fetchChatMessages 함수 =================
// 기존 엔드포인트 '/chat/api/messages' 사용 (여기서는 그대로 사용)
function fetchChatMessages(chatStreamNo) {
    const accessToken = sessionStorage.getItem('accessToken');
    console.log("[DEBUG] fetchChatMessages 호출됨. chatStreamNo:", chatStreamNo);

    axios.get('/chat/api/messages', {
        params: { chatStreamNo: chatStreamNo },
        headers: {
            "Authorization": "Bearer " + accessToken
        }
    })
        .then(response => {
            console.log("[DEBUG] fetchChatMessages 응답 데이터:", response.data);
            chatMessages = response.data; // 전역 변수에 할당
            console.log("[DEBUG] 전역 chatMessages 업데이트됨:", chatMessages);

            // 각 메시지에 대해 offsetSeconds 계산
            chatMessages.forEach(msg => {
                msg.offsetSeconds = getOffsetSeconds(msg.chat_created_at);
                console.log("[DCKAT] msg.offsetSeconds", msg.offsetSeconds);
            });

            // offsetSeconds 기준 오름차순 정렬
            chatMessages.sort((a, b) => a.offsetSeconds - b.offsetSeconds);
            console.log("[DEBUG] 정렬된 chatMessages:", chatMessages);
            nextChatIndex = 0;
        })
        .catch(error => {
            console.error("채팅 메시지를 불러오는 중 오류 발생:", error);
        });
}

function updateChatMessages() {
    const currentSec = videoElement.currentTime;  // 'vod' 비디오 요소의 currentTime 사용
    console.log("[DEBUG] currentTime >>", currentSec);
    console.log("[DEBUG] updateChatMessages - nextChatIndex:", nextChatIndex, " / 전체 메시지 수:", chatMessages.length);

    // 각 메시지에 대해 반복하면서 현재 재생 시간과 offsetSeconds 비교
    chatMessages.forEach((msg, idx) => {
        console.log(`[DEBUG] 원본 timestampStr(${idx}):`, msg.chat_created_at);
        let fixedStr = msg.chat_created_at.replace(/(\.\d{3})\d+/, '$1')
            .replace(' ', 'T');
        console.log(`[DEBUG] 변환 후 fixedStr(${idx}):`, fixedStr);
        let parsedDate = new Date(fixedStr);
        console.log(`[DEBUG] parsedDate(${idx}):`, parsedDate);

        // parsedDate가 Invalid Date인지 확인
        if (isNaN(parsedDate.getTime())) {
            console.error(`[ERROR] Date 파싱 실패(${idx}):`, fixedStr);
        }

        msg.offsetSeconds = (parsedDate - window.streamStartTime) / 1000;
    });

    // nextChatIndex부터 모든 메시지 중 아직 출력되지 않은 메시지에 대해 검사
    for (let i = nextChatIndex; i < chatMessages.length; i++) {
        const msg = chatMessages[i];
        // 로그: 각 메시지에 대한 offsetSeconds와 비교 결과
        console.log(`[DEBUG] 비교 메시지 인덱스 ${i}: offsetSeconds = ${msg.offsetSeconds}, currentSec = ${currentSec}`);
        if (!msg.displayed && msg.offsetSeconds <= currentSec) {
            console.log(`[DEBUG] 메시지 인덱스 ${i} 조건 만족 -> 출력`);
            appendChatMessage(msg);
            msg.displayed = true;
            nextChatIndex = i + 1;  // 출력된 이후 다음 인덱스로 업데이트
        } else {
            console.log(`[DEBUG] 메시지 인덱스 ${i} 조건 불만족 -> 보류`);
            // 조건에 맞지 않으면 break: 아직 출력할 메시지가 없으므로 루프 종료
            break;
        }
    }
    requestAnimationFrame(updateChatMessages);
}

// ================= 수정된 appendChatMessage 함수 =================
// 채팅 메시지를 DOM의 #chat-messages 컨테이너에 추가 (사용자 아이디와 채팅 내역만 표시)
function appendChatMessage(msg) {
    const chatContainer = document.getElementById('chat-messages');
    const messageElem = document.createElement('div');
    messageElem.className = 'chat-message';  // 클래스명이 'chat-message'
    // 데이터 키: chat_member_id, chat_message (POSTMAN 응답과 일치)
    messageElem.textContent = `[${msg.chat_member_id}] ${msg.chat_message}`;
    chatContainer.appendChild(messageElem);
    console.log("[DEBUG] 채팅 메시지 추가됨:", msg);
    // 자동 스크롤
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

function addWatch(streamNo) {
    const accessToken = sessionStorage.getItem('accessToken');
    const watchTime = new Date();
    
    axios.post('/watch/insert',
    {
        streamNo: streamNo,
        watchTime: watchTime
    }, {
        headers: {
            "Authorization": "Bearer " + accessToken
        }
    });
}

// 자막을 불러오는 메서드
function fetchSubtitle(title) {
    // 최초에는 자막모드를 비활성화
    track.mode = "disabled";

    // axios 활용 자막정보 가져오기
    axios.get(`/stream/subtitle/${title}.json`)
        .then(response => response.data)
        .then(data => {
            // segment 별로 TextTrack 추가
            data.segments.forEach(segment => {
                let cue = new VTTCue(
                    msToSeconds(segment.start),
                    msToSeconds(segment.end),
                    ""
                );
                cue.words = segment.words;
                cue.fullText = segment.text;
                track.addCue(cue);
            })
        });
}

// 밀리초를 초 단위로 변환하는 함수
function msToSeconds(ms) {
    return ms / 1000;
}

async function streamVideo(title) {
    if (Hls.isSupported()) {
        var hls = new Hls();
        hls.loadSource(`/hls/v2/flux/${title}.m3u8`);
        hls.attachMedia(videoElement);
    }
    else if (videoElement.canPlayType('application/vnd.apple.mpegurl')) {
        videoElement.src = `/stream/vod/${title}.m3u8`;
    }
}

// 자막모드 활성화
function onSubtitle() {
    if (track.mode === "disabled") {
        track.mode = "showing";
    }
}

// 자막모드 비활성화
function offSubtitle() {
    if (track.mode === "showing") {
        track.mode = "disabled";
    }
}

// 영상의 시간이 바뀔대마다 자막정보 update
videoElement.addEventListener('timeupdate', () => {
    // updateChatMessages 함수가 들어갔던 부분
    // 자막모드가 비활성화일때는 수행하지 않음
    if (track.mode !== 'showing') {
        return;
    }

    // 현재 활성화된 자막 가져오기
    if (track.activeCues && track.activeCues.length > 0) {
        const activeCue = track.activeCues[0]; // 활성 cue 하나를 대상으로 처리
        let currentTime = videoElement.currentTime;
        let displayedText = "";

        // 어절별로 자막을 붙여서 자막 표현
        activeCue.words.forEach(word => {
            // word.start가 밀리초 단위이므로 비교 전 변환
            if ((currentTime * 1000) >= word.start) {
                displayedText += (displayedText ? " " : "") + word.text;
            }
        });
        activeCue.text = displayedText;
    }
});

function controlTabs() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    const faqItems = document.querySelectorAll('.faq-item');

    faqItems.forEach(item => {
        const question = item.querySelector('.faq-question');

        // 질문 영역 클릭 시 active 토글
        question.addEventListener('click', () => {
            // 이미 active라면 닫고, 아니면 열기
            item.classList.toggle('active');
        });
    });

    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            // 1) 모든 버튼에서 active 제거
            tabButtons.forEach(b => b.classList.remove('active'));
            // 2) 모든 탭 콘텐츠에서 active 제거
            tabContents.forEach(tc => tc.classList.remove('active'));

            // 3) 클릭된 버튼에 active 추가
            btn.classList.add('active');
            // 4) data-target 속성으로 연결된 콘텐츠를 찾아서 active 추가
            const targetId = btn.getAttribute('data-target');
            const targetContent = document.getElementById(targetId);
            if (targetContent) {
                targetContent.classList.add('active');
            }
        });
    });
}
