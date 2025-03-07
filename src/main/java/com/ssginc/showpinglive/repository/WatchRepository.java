package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.dto.response.WatchResponseDto;
import com.ssginc.showpinglive.entity.Watch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchRepository extends JpaRepository<Watch, Long> {

    @Query("""
        SELECT new com.ssginc.showpinglive.dto.response.WatchResponseDto
        (w.stream.streamNo, s.streamTitle, p.productImg, p.productName, p.productPrice, MAX(w.watchTime))
        FROM Watch w JOIN Stream s ON w.stream.streamNo = s.streamNo
        JOIN Product p ON s.product.productNo = p.productNo WHERE w.member.memberNo = :memberNo
        GROUP BY w.stream.streamNo, s.streamTitle, p.productImg, p.productName, p.productPrice
    """)
    List<WatchResponseDto> getWatchListByMemberNo(Long memberNo);

}
