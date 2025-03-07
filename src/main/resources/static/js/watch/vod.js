window.onload = async function () {
    const title = document.getElementById('vod').getAttribute('value');
    await streamVideo(title);
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