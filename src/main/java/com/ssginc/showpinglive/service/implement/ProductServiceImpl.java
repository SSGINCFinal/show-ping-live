package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.dto.object.ProductItemDto;
import com.ssginc.showpinglive.dto.response.ProductDto;
import com.ssginc.showpinglive.entity.Product;
import com.ssginc.showpinglive.repository.ProductRepository;
import com.ssginc.showpinglive.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

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

    public List<ProductItemDto> getProducts() {
        try {
            List<Product> products = productRepository.findAll();

            if (products.isEmpty()) {
                throw new RuntimeException("상품이 없습니다.");
            }

            return products.stream().map(product -> {
                Long productPrice = product.getProductPrice();

                return ProductItemDto.builder()
                        .productNo(product.getProductNo())
                        .productName(product.getProductName())
                        .productPrice(productPrice)
                        .productImg(product.getProductImg())
                        .build();
            }).toList();
        } catch (RuntimeException e) {
            log.error("Exception [Err_Msg]: {}", e.getMessage());
            log.error("Exception [Err_Where]: {}", e.getStackTrace()[0]);

            return null;
        }
    }

}
