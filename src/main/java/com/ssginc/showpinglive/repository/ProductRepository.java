package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 카테고리 번호에 해당하는 상품과 해당 상품의 평균 리뷰 평점 및 리뷰 개수를 가져오는 쿼리
    @Query("SELECT p, AVG(r.reviewRating) AS reviewAverage, COUNT(r) AS reviewCount " +
            "FROM Product p " +
            "LEFT JOIN p.reviews r " +
            "WHERE (:categoryNo = 0 OR p.category.categoryNo = :categoryNo) " +
            "GROUP BY p.productNo")
    Page<Object[]> findByCategoryCategoryNo(@Param("categoryNo") Long categoryNo, Pageable pageable);

}