package com.ssginc.showpinglive;

import com.ssginc.showpinglive.dto.request.RegisterStreamRequestDto;
import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.entity.Product;
import com.ssginc.showpinglive.entity.Stream;
import com.ssginc.showpinglive.repository.MemberRepository;
import com.ssginc.showpinglive.repository.ProductRepository;
import com.ssginc.showpinglive.repository.StreamRepository;
import com.ssginc.showpinglive.service.implement.StreamServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StreamServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StreamRepository streamRepository;

    @InjectMocks
    private StreamServiceImpl streamService;

    @Test
    public void testCreateStream_NewStream() {
        // 테스트 데이터 (신규 생성: streamNo == null)
        RegisterStreamRequestDto request = new RegisterStreamRequestDto();
        request.setStreamNo(null);
        request.setProductNo(1L);
        request.setStreamTitle("신규 방송 제목");
        request.setStreamDescription("신규 방송 설명");
        request.setProductSale(10);

        // 모킹: productRepository 반환
        Product product = new Product();
        product.setProductNo(1L);
        product.setProductSale(0);  // 초기 할인율 0
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // 모킹: memberRepository 반환
        Member member = new Member();
        member.setMemberId("member1");
        when(memberRepository.findByMemberId("member1")).thenReturn(Optional.of(member));

        // 모킹: streamRepository save 동작 (신규 생성 시 streamNo 100L 부여)
        Stream savedStream = new Stream();
        savedStream.setStreamNo(100L);
        when(streamRepository.save(any(Stream.class))).thenReturn(savedStream);

        // 서비스 메서드 실행
        Long responseStreamNo = streamService.createStream("member1", request);

        // 검증
        assertNotNull(responseStreamNo, "반환된 streamNo는 null 이면 안됨");
        assertEquals(100L, responseStreamNo, "신규 생성된 streamNo가 예상값과 일치해야 함");
        // product의 할인율이 올바르게 반영되었는지 확인
        assertEquals(10, product.getProductSale(), "상품 할인율이 신규 요청 값으로 설정되어야 함");
    }

    @Test
    public void testCreateStream_UpdateExistingStream() {
        // 테스트 요청 데이터
        RegisterStreamRequestDto request = new RegisterStreamRequestDto();
        request.setStreamNo(200L);
        request.setProductNo(2L);
        request.setStreamTitle("수정 방송 제목");
        request.setStreamDescription("수정 방송 설명");
        request.setProductSale(20);

        // 모킹: productRepository 반환
        Product product = new Product();
        product.setProductNo(2L);
        product.setProductSale(0);  // 초기 할인율 0
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        // 기존 스트림 엔티티 모킹 (수정 전)
        Stream existingStream = new Stream();
        existingStream.setStreamNo(200L);
        existingStream.setStreamTitle("기존 방송 제목");
        existingStream.setStreamDescription("기존 방송 설명");
        // 기존 스트림이 연결된 상품 (수정 전 상품 할인율 0)
        Product oldProduct = new Product();
        oldProduct.setProductNo(999L);
        oldProduct.setProductSale(5);
        existingStream.setProduct(oldProduct);

        when(streamRepository.findById(200L)).thenReturn(Optional.of(existingStream));
        // 수정 후 저장 시 동일 엔티티 반환 (streamNo 동일)
        when(streamRepository.save(any(Stream.class))).thenReturn(existingStream);

        // 서비스 메서드 실행
        Long responseStreamNo = streamService.createStream("member1", request);

        // 검증
        assertNotNull(responseStreamNo, "반환된 streamNo는 null 이면 안됨");
        assertEquals(200L, responseStreamNo, "수정된 streamNo가 기존 값과 동일해야 함");

        // 기존 스트림의 제목, 설명이 수정되었는지 확인
        assertEquals("수정 방송 제목", existingStream.getStreamTitle(), "스트림 제목이 수정되어야 함");
        assertEquals("수정 방송 설명", existingStream.getStreamDescription(), "스트림 설명이 수정되어야 함");
        // 기존 스트림이 연결된 상품이 수정되었는지 확인
        assertEquals(request.getProductNo(), existingStream.getProduct().getProductNo(), "새로운 상품 번호는 요청의 상품 번호이어야 함");
        // 기존 상품의 할인율은 0으로 초기화 되었는지, 그리고 새로 선택된 상품의 할인율이 반영되었는지 검증
        assertEquals(0, oldProduct.getProductSale(), "기존 상품 할인율은 0이어야 함");
        assertEquals(20, product.getProductSale(), "새로운 상품 할인율이 요청 값으로 설정되어야 함");
    }

}
