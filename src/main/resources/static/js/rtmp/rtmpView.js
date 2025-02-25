document.addEventListener("DOMContentLoaded", function () {
    var video = document.getElementById('video');
    var videoUrl = 'http://localhost:7070/hls/test.m3u8';  // HLS 스트림 URL

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