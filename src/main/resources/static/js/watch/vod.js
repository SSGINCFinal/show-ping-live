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
        hls.loadSource(`/hls/v2/${title}.m3u8`);
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
