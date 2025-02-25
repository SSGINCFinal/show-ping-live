let mediaRecoder;
let ws;

const videoElement = document.getElementById("video");
const startButton = document.getElementById("start");
const stopButton = document.getElementById("stop");

let stream;

async function startCamera() {
    try {
        stream = await navigator.mediaDevices.getUserMedia({
            video: { width: 300, height: 480, },
            audio: true
        });
        console.log(stream)
        videoElement.srcObject = stream;
    } catch (err) {
        console.error("웹캠 접근 오류", err);
    }
}

function startStreaming() {
    if (!stream) {
        alert("웹캠을 먼저 실행해야 합니다.");
        return;
    }

    ws = new WebSocket("ws://" + location.host + "/stream");
    ws.binaryType = "arraybuffer";

    ws.onopen = () => {
        console.log("WebSocket 연결됨: 스트리밍 시작");
        mediaRecoder = new MediaRecorder(stream, {mimeType: "video/webm; codecs=vp8"});

        mediaRecoder.ondataavailable = (event) => {
            if (event.data.size > 0 && ws.readyState === WebSocket.OPEN) {
                ws.send(event.data);
            }
        };

        mediaRecoder.start(100);
        startButton.disabled = true;
        stopButton.disabled = false;
    };

    ws.onclose = () => {
        console.log("WebSocket 연결 종료됨");
    }

    ws.onerror = (error) => {
        console.log("WebSocket 오류: ", error);
    };

}

function stopStreaming() {
    if (mediaRecoder) {
        mediaRecoder.stop();
    }

    if (ws) {
        ws.close();
    }

    startButton.disabled = false;
    stopButton.disabled = true;
}

window.onload = startCamera;
startButton.addEventListener("click", startStreaming);
stopButton.addEventListener("click", stopStreaming);
