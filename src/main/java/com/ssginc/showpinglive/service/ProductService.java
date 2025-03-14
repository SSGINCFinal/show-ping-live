package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.ProductItemDto;
import com.ssginc.showpinglive.dto.response.ProductDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {

    public List<ProductDto> getProductsByCategory(Long categoryNo);

    public ProductDto getProductById(Long productId);

    public List<ProductItemDto> getProducts();

}
