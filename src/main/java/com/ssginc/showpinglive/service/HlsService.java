package com.ssginc.showpinglive.service;

import reactor.core.publisher.Mono;

public interface HlsService {

    Mono<?> getHLS(String title);

    Mono<?> getTsSegment(String title, String segment);

}
