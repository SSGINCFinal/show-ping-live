package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.dto.request.WatchRequestDto;
import com.ssginc.showpinglive.dto.response.WatchResponseDto;
import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.entity.Stream;
import com.ssginc.showpinglive.entity.Watch;
import com.ssginc.showpinglive.repository.WatchRepository;
import com.ssginc.showpinglive.service.WatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchServiceImpl implements WatchService {

    private final WatchRepository watchRepository;

    @Override
    public List<WatchResponseDto> getWatchHistoryByMemberNo(Long memberNo) {
        return watchRepository.getWatchListByMemberNo(memberNo);
    }

    @Override
    public Watch insertWatchHistory(WatchRequestDto watchRequestDto) {
        Stream stream = Stream.builder()
                .streamNo(watchRequestDto.getStreamNo())
                .build();

        Member member = Member.builder()
                .memberNo(watchRequestDto.getMemberNo())
                .build();

        Watch watch = Watch.builder()
                .stream(stream)
                .member(member)
                .watchTime(watchRequestDto.getWatchTime())
                .build();

        return watchRepository.save(watch);
    }

}
