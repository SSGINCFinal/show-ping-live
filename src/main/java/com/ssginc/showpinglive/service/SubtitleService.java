package com.ssginc.showpinglive.service;


import org.springframework.core.io.Resource;

public interface SubtitleService {

    void createSubtitle(String title);

    Resource getSubtitle(String title);

}
