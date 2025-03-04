package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("stream")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

    @GetMapping(value = "/vod/fetch/{title}", produces = "video/mp4")
    public Mono<ResponseEntity<Resource>> fetchVod(@PathVariable String title,
                                                   @RequestHeader(value = "Range", required = false) String range) {

        return streamService.getVideo(title)
                .map(resource -> {
                    long contentLength;
                    try {
                        contentLength = resource.contentLength();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    if (range == null || range.isEmpty()) {
                        return ResponseEntity.ok()
                                .contentType(MediaTypeFactory.getMediaType(resource)
                                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                                .body(resource);
                    }

                    String[] ranges = range.replace("bytes=", "").split("-");
                    long start = Long.parseLong(ranges[0]);
                    long end = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : contentLength - 1;

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Range", "bytes " + start + "-" + end + "/" + contentLength);
                    headers.setContentLength(end - start + 1);
                    headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");

                    InputStreamResource partialResource;
                    try {
                        partialResource = new InputStreamResource(resource.getInputStream()) {
                            @Override
                            public long contentLength() {
                                return end - start + 1;
                            }
                        };
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                            .headers(headers)
                            .contentType(MediaTypeFactory.getMediaType(resource)
                                    .orElse(MediaType.APPLICATION_OCTET_STREAM))
                            .body(partialResource);
                });
    }

    @PostMapping("/vod/upload")
    public ResponseEntity<String> uploadVod(@RequestBody Map<String, String> requestData) {
        System.out.println(requestData);
        String title = requestData.get("title");
        System.out.println(title);
        return ResponseEntity.ok(streamService.uploadVideo(title));
    }
}
