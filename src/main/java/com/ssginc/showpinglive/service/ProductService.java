package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.response.ProductDto;
import com.ssginc.showpinglive.entity.Product;
import com.ssginc.showpinglive.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDto> getProductsByCategory(Long categoryNo) {
        return productRepository.findByCategoryCategoryNo(categoryNo)
                .stream()
                .map(product -> new ProductDto(
                        product.getProductNo(),
                        product.getProductName(),
                        product.getProductPrice(),
                        product.getProductQuantity(),
                        product.getProductImg(),
                        product.getProductDescript()
                ))
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return new ProductDto(
                    product.getProductNo(),
                    product.getProductName(),
                    product.getProductPrice(),
                    product.getProductQuantity(),
                    product.getProductImg(),
                    product.getProductDescript()
            );
        }else{
            throw new RuntimeException("상품을 찾을 수 없습니다: " + productId);
        }
    }
}
