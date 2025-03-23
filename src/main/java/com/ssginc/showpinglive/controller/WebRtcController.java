package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.response.GetStreamProductInfoResponseDto;
import com.ssginc.showpinglive.dto.response.GetStreamRegisterInfoResponseDto;
import com.ssginc.showpinglive.service.StreamService;
import com.ssginc.showpinglive.dto.response.ChatRoomResponseDto;
import com.ssginc.showpinglive.entity.ChatRoom;
import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.service.ChatRoomService;
import com.ssginc.showpinglive.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("webrtc")
@RequiredArgsConstructor
public class WebRtcController {

    private final StreamService streamService;
  
    private final MemberService memberService;
  
    private final ChatRoomService chatRoomService;

    @GetMapping("webrtc")
    public String webrtc(@AuthenticationPrincipal UserDetails userDetails, Model model) {
//        System.out.println(userDetails);
//
//        String memberId = null;
//        if (userDetails != null) {
//            memberId = userDetails.getUsername();   // 로그인한 멤버 아이디
//        }
//        System.out.println(memberId);
//
//        GetStreamRegisterInfoResponseDto streamInfo = streamService.getStreamRegisterInfo(memberId);
//
//        System.out.println("########################");
//        System.out.println(streamInfo);
//        System.out.println("########################");
//
//        model.addAttribute("streamInfo", streamInfo);
//        model.addAttribute("memberId", memberId);

        return "webrtc/webrtc";
    }

    @GetMapping("watch/{streamNo}")
    public String watch(@PathVariable Long streamNo, Model model) {

        GetStreamProductInfoResponseDto streamProductInfo = streamService.getStreamProductInfo(streamNo);
        ChatRoomResponseDto chatRoom = chatRoomService.findChatRoomByStreamNo(streamNo);

        // 가격 문자열에서 숫자와 소수점만 남김 (쉼표, 원 등의 문자는 제거)
        String rawPrice = streamProductInfo.getProductPrice().replaceAll("[^\\d.]", "");
        String rawSale = streamProductInfo.getProductSalePrice().replaceAll("[^\\d.]", "");

        int price = Integer.parseInt(rawPrice);
        int sale = Integer.parseInt(rawSale);

        // 할인 금액: (상품 가격 - 할인가격)
        int discountAmount = price - sale;
        // 할인율: ((할인 금액 / 상품 가격) * 100)
        int discountRate = (price > 0) ? (discountAmount * 100) / price : 0;

        model.addAttribute("chatRoomInfo", chatRoom);
        model.addAttribute("productInfo", streamProductInfo);
        model.addAttribute("discountRate", discountRate);

        return "webrtc/watch";
    }
}
