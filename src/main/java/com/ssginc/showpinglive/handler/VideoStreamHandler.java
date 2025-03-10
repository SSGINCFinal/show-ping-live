package com.ssginc.showpinglive.handler;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoStreamHandler extends BinaryWebSocketHandler {

    private Process ffmpegProcess;
    private OutputStream ffmpegInput;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String RTMP_URL = "rtmp://showping.duckdns.org:1935/live/stream";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        startFFmpegProcess();
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        if (ffmpegInput != null) {
            ffmpegInput.write(message.getPayload().array());
            ffmpegInput.flush();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        stopFFmpegProcess();
    }

    private void startFFmpegProcess() {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", "pipe:0",
                    "-f", "flv",
                    "-s", "720x1280",
                    "-c:v", "libx264",
                    "-preset", "ultrafast",
                    "-tune", "zerolatency",
                    "-b:v", "2500k",
                    "-c:a", "aac",
                    "-b:a", "128k",
                    RTMP_URL
            );
            builder.redirectErrorStream(true);
            ffmpegProcess = builder.start();
            ffmpegInput = ffmpegProcess.getOutputStream();

            executorService.submit(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpegProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("***************************");
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("111111111111111111");
                    e.printStackTrace();
                }
            });
        } catch (IOException exception) {
            System.out.println("2222222222222222222");
            exception.printStackTrace();
        }
    }

    private void stopFFmpegProcess() {
        if (ffmpegProcess != null) {
            ffmpegProcess.destroy();
            ffmpegProcess = null;
        }
    }
}
