package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.response.VodResponseDto;

public interface StreamService {

    VodResponseDto getVodByNo(Long streamNo);

}
