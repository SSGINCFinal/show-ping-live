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

/**
 * @author dckat
 * 영상 시청과 관련한 로직을 처리하는 서비스 layer 클래스
 * <p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WatchServiceImpl implements WatchService {

    private final WatchRepository watchRepository;

    /**
     * 로그인한 사용자의 시청내역 리스트를 반환하는 컨트롤러 메소드
     * @param memberNo 로그인한 사용자 번호
     * @return 로그인한 사용자의 시청내역 리스트
     */
    @Override
    public List<WatchResponseDto> getWatchHistoryByMemberNo(Long memberNo) {
        return watchRepository.getWatchListByMemberNo(memberNo);
    }

    /**
     * 시청 내역 등록 서비스 layer 메소드
     * @param watchRequestDto 시청내역 등록을 위한 요청 DTO
     * @return 추가된 시청내역 객체
     */
    @Override
    public Watch insertWatchHistory(WatchRequestDto watchRequestDto) {
        // 영상 엔티티 객체 생성 (빌더 패턴)
        Stream stream = Stream.builder()
                .streamNo(watchRequestDto.getStreamNo())
                .build();

        // 사용자 엔티티 객체 생성
        // 로그인하지 않은 사용자도 insert 하기 위해 우선적으로 null 할당
        Member member = null;

        // 로그인한 사용자가 존재한 경우
        if (watchRequestDto.getMemberNo() != null) {
            member = Member.builder()
                    .memberNo(watchRequestDto.getMemberNo())
                    .build();
        }

        // DB 저장을 위한 엔티티 객체 생성 (빌더 패턴)
        Watch watch = Watch.builder()
                .stream(stream)
                .member(member)
                .watchTime(watchRequestDto.getWatchTime())
                .build();

        return watchRepository.save(watch);
    }

}
