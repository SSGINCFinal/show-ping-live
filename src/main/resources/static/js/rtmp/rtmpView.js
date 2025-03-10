document.addEventListener("DOMContentLoaded", function () {
    var video = document.getElementById('video');
    var videoUrl = 'https://showping.duckdns.org/hls/stream.m3u8';  // HLS 스트림 URL

    if (Hls.isSupported()) {
        var hls = new Hls();
        hls.loadSource(videoUrl);
        hls.attachMedia(video);
        hls.on(Hls.Events.MANIFEST_PARSED, function () {
            video.play();
        });
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
        video.src = videoUrl;
        video.addEventListener('loadedmetadata', function () {
            video.play();
        });
    }
});