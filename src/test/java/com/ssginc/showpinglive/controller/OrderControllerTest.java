package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.request.OrderRequestDto;
import com.ssginc.showpinglive.dto.response.OrdersDto;
import com.ssginc.showpinglive.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderControllerTest {

    @Autowired
    private OrderService orderService;

    @Test
    void createOrder() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // OrderRequestDto 초기화 (실제 필요한 필드로 대체)
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setMemberNo(1L);  // 테스트용 회원 번호 (적절히 변경)
        // 필요한 기타 주문 데이터도 설정 (예: 상품 목록, 결제 정보 등)

        // 동시에 numberOfThreads 개의 주문 생성 요청 실행
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    orderService.createOrder(orderRequestDto);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기 (최대 10초)
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // 주문 생성 결과 검증
        // 같은 회원에 대해 주문 생성 요청을 보냈으므로, 전체 주문 건수가 numberOfThreads개여야 함.
        List<OrdersDto> orders = orderService.findAllOrdersByMember(orderRequestDto.getMemberNo());
        assertEquals(numberOfThreads, orders.size(), "모든 동시 주문이 정상적으로 생성되어야 합니다.");
    }
}