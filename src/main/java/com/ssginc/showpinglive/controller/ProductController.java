package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.response.ProductDto;
import com.ssginc.showpinglive.dto.response.ReviewDto;
import com.ssginc.showpinglive.service.ProductService;
import com.ssginc.showpinglive.service.implement.ReviewServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ReviewServiceImpl reviewService;

    @GetMapping("/{categoryNo}")
    public Page<ProductDto> getProductsByCategory(
            @PathVariable Long categoryNo,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort) {

        Pageable pageable = PageRequest.of(page, size, getSort(sort)); // sort에 따른 정렬 방식 설정
        return productService.getProductsByCategory(categoryNo, pageable);
    }

    @GetMapping("/detail/{productNo}")
    public ProductDto getProductDetail(@PathVariable Long productNo) {
        return productService.getProductById(productNo);
    }

    @GetMapping("/reviews/{productNo}")
    public List<ReviewDto> getProductReviews(@PathVariable Long productNo) {
        return reviewService.getReviewsByProductNo(productNo);
    }

    private Sort getSort(String sortOption) {
        switch (sortOption) {
            case "quantity-desc":
                return Sort.by(Sort.Order.desc("productQuantity"));
            case "quantity-asc":
                return Sort.by(Sort.Order.asc("productQuantity"));
            case "price-desc":
                return Sort.by(Sort.Order.desc("productPrice"));
            case "price-asc":
                return Sort.by(Sort.Order.asc("productPrice"));
            case "sale-desc":
                return Sort.by(Sort.Order.desc("productSale"));
            case "sale-asc":
                return Sort.by(Sort.Order.asc("productSale"));
            default:
                return Sort.by(Sort.Order.desc("productQuantity"));
        }
    }

}