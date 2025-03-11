let currentTimer = null;
const videoElement = document.getElementById('vod');
const track = videoElement.addTextTrack("subtitles", "Korean", "ko");

function addWatch(memberNo, streamNo) {
    const watchTime = new Date();
    
    axios.post('/watch/insert', {
        memberNo: memberNo,
        streamNo: streamNo,
        watchTime: watchTime
    });
}

function fetchSubtitle(title) {
    track.mode = "disabled";
    axios.get(`/stream/subtitle/${title}.json`)
        .then(response => response.data)
        .then(data => {
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
        hls.loadSource(`/stream/vod/${title}.m3u8`);
        hls.attachMedia(videoElement);
    }
    else if (videoElement.canPlayType('application/vnd.apple.mpegurl')) {
        videoElement.src = `/stream/vod/${title}.m3u8`;
    }
}

function onSubtitle() {
    if (track.mode === "disabled") {
        track.mode = "showing";
    }
}

function offSubtitle() {
    if (track.mode === "showing") {
        track.mode = "disabled";
    }
}

videoElement.addEventListener('timeupdate', () => {
    if (track.mode !== 'showing') {
        return;
    }

    if (track.activeCues && track.activeCues.length > 0) {
        const activeCue = track.activeCues[0]; // 활성 cue 하나를 대상으로 처리
        let currentTime = videoElement.currentTime;
        let displayedText = "";

        activeCue.words.forEach(word => {
            // word.start가 밀리초 단위이므로 비교 전 변환
            if ((currentTime * 1000) >= word.start) {
                displayedText += (displayedText ? " " : "") + word.text;
            }
        });
        activeCue.text = displayedText;
    }
});
