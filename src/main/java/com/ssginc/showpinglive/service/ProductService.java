package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.ProductItemDto;
import com.ssginc.showpinglive.dto.response.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    public Page<ProductDto> getProductsByCategory(Long categoryNo, Pageable pageable);

    public ProductDto getProductById(Long productId);

    public List<ProductItemDto> getProducts();

    public List<ProductDto> getTopProductsBySaleQuantity(Long categoryNo);

    public List<ProductDto> getTopProductsBySale(Long categoryNo);

}
