package com.ssginc.showpinglive;

import com.ssginc.showpinglive.config.WebSocketConfig;
import com.ssginc.showpinglive.dto.object.GetStreamRegisterInfoDto;
import com.ssginc.showpinglive.entity.*;
import com.ssginc.showpinglive.repository.MemberRepository;
import com.ssginc.showpinglive.repository.ProductRepository;
import com.ssginc.showpinglive.repository.StreamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class StreamRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StreamRepository streamRepository;

    private Member member;
    private Product product;
    private Stream stream;

    @BeforeEach
    public void setUp() {
        member = new Member();
        member.setMemberId("testMember");
        member.setMemberName("test");
        member.setMemberPassword("testPassword");
        member.setMemberEmail("000");
        member.setMemberRole(MemberRole.ROLE_ADMIN);
        member.setStreamKey("11111");
        member.setMemberAddress("222222");
        member = memberRepository.save(member);

        product = new Product();
        product.setCategory(new Category(1L, "temp"));
        product.setProductName("테스트 상품");
        product.setProductPrice(10000L);
        product.setProductSale(15);
        product.setProductImg("test_img.png");
        product = productRepository.save(product);

        stream = new Stream();
        stream.setStreamTitle("테스트 스트림");
        stream.setStreamDescription("테스트 설명");
        stream.setStreamStatus(StreamStatus.STANDBY);
        stream.setMember(member);
        stream.setProduct(product);
        stream = streamRepository.save(stream);
    }

    @Test
    public void testFindStreamByMemberIdAndStreamStatus() {
        GetStreamRegisterInfoDto dto = streamRepository.findStreamByMemberIdAndStreamStatus("testMember");

        assertNotNull(dto, "DTO는 null이 아니어야 합니다.");
        assertEquals(stream.getStreamNo(), dto.getStreamNo(), "스트림 번호가 일치해야 합니다.");
        assertEquals(stream.getStreamTitle(), dto.getStreamTitle(), "스트림 제목이 일치해야 합니다.");
        assertEquals(stream.getStreamDescription(), dto.getStreamDescription(), "스트림 설명이 일치해야 합니다.");
        assertEquals(product.getProductNo(), dto.getProductNo(), "상품 번호가 일치해야 합니다.");
        assertEquals(product.getProductName(), dto.getProductName(), "상품 이름이 일치해야 합니다.");
        assertEquals(product.getProductPrice(), dto.getProductPrice(), "상품 가격이 일치해야 합니다.");
        assertEquals(product.getProductSale(), dto.getProductSale(), "상품 할인율이 일치해야 합니다.");
        assertEquals(product.getProductImg(), dto.getProductImg(), "상품 이미지가 일치해야 합니다.");
    }

}
