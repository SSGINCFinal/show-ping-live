package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.request.CartRequestDto;
import com.ssginc.showpinglive.dto.response.CartDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    //회원(memberNo)의 장바구니 조회
    List<CartDto> getCartByMemberNo(Long memberNo);

    //장바구니에 상품 추가 (중복 상품이면 수량 증가)
    @Transactional
    void addToCart(Long memberNo, CartRequestDto requestDTO);

    //장바구니 수정
    @Transactional
    void updateCartItem(Long memberNo, CartRequestDto requestDTO);

    //장바구니 삭제
    @Transactional
    void removeCartItem(Long memberNo, Long productNo);
}
