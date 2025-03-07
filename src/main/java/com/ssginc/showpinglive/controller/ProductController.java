package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.response.ProductDto;
import com.ssginc.showpinglive.dto.response.ReviewDto;
import com.ssginc.showpinglive.service.ProductService;
import com.ssginc.showpinglive.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{categoryNo}")
    public List<ProductDto> getProductsByCategory(@PathVariable Long categoryNo) {
        return productService.getProductsByCategory(categoryNo);
    }

    @GetMapping("/detail/{productNo}")
    public ProductDto getProductDetail(@PathVariable Long productNo) {
        return productService.getProductById(productNo);
    }

    @GetMapping("/reviews/{productNo}")
    public List<ReviewDto> getProductReviews(@PathVariable Long productNo) {
        return reviewService.getReviewsByProductNo(productNo);
    }

}
