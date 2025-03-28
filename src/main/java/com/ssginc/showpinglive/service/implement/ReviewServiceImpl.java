package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.dto.response.ReviewDto;
import com.ssginc.showpinglive.repository.ReviewRepository;
import com.ssginc.showpinglive.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public List<ReviewDto> getReviewsByProductNo(Long productNo) {
        return reviewRepository.findByProductProductNo(productNo).stream()
                .map(review -> new ReviewDto(
                        review.getReviewNo(),
                        review.getMember().getMemberName(), // 회원 이름 가져오기
                        review.getReviewRating(),
                        review.getReviewComment(),
                        review.getReviewCreateAt(),
                        review.getReviewUrl()
                ))
                .collect(Collectors.toList());
    }
}
