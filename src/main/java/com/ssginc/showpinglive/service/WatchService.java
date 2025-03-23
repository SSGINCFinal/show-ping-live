package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.request.WatchRequestDto;
import com.ssginc.showpinglive.dto.response.WatchResponseDto;
import com.ssginc.showpinglive.entity.Watch;

import java.util.List;

public interface WatchService {

    List<WatchResponseDto> getWatchHistoryByMemberNo(Long memberNo);

    Watch insertWatchHistory(WatchRequestDto watchRequestDto, Long memberNo);

}
