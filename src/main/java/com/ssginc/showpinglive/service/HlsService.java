package com.ssginc.showpinglive.service;

import reactor.core.publisher.Mono;

import java.io.IOException;

public interface HlsService {

    Mono<?> getHLSV1(String title);

    Mono<?> getTsSegmentV1(String title, String segment);

    String saveHLS(String title) throws IOException, InterruptedException;

    Mono<?> getHLSV2(String title);

    Mono<?> getTsSegmentV2(String title, String segment);

}
