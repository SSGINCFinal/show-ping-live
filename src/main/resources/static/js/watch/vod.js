function addWatch(memberNo, streamNo) {
    const watchTime = new Date();
    
    axios.post('/watch/insert', {
        memberNo: memberNo,
        streamNo: streamNo,
        watchTime: watchTime
    });
}

async function streamVideo(title) {
    const videoElement = document.getElementById('vod');

    if (Hls.isSupported()) {
        var hls = new Hls();
        hls.loadSource(`/stream/vod/${title}.m3u8`);
        hls.attachMedia(videoElement);
    }
    else if (videoElement.canPlayType('application/vnd.apple.mpegurl')) {
        videoElement.src = `/stream/vod/${title}.m3u8`;
    }
}