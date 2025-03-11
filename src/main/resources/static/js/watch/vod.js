let subtitles = [];
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
                const cue = new VTTCue(
                    msToSeconds(segment.start),
                    msToSeconds(segment.end),
                    segment.text
                );
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
