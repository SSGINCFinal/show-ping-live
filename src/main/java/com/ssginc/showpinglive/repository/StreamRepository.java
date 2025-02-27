package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.dto.response.VodResponseDto;
import com.ssginc.showpinglive.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * 영상데이터 DB 쿼리를 통해 가져오는 인터페이스
 * <p>
 */
@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {

    /**
     * 특정 영상번호의 Vod 정보를 반환해주는 쿼리 메소드
     * @param streamNo 영상 번호
     * @return vod 목록
     */
    @Query("""
        SELECT new com.ssginc.showpinglive.dto.response.VodResponseDto
        (s.streamNo, s.streamTitle, c.categoryNo, c.categoryName, p.productName,
        p.productPrice, p.productSale, p.productImg, s.streamStartTime, s.streamEndTime)
        FROM Stream s JOIN Product p ON s.product.productNo = p.productNo
        JOIN Category c ON p.category.categoryNo = c.categoryNo WHERE s.streamNo = :streamNo
    """)
    VodResponseDto findVodByNo(Long streamNo);
}