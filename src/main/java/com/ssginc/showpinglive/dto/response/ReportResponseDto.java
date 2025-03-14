package com.ssginc.showpinglive.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDto {
    private Long reportNo;
    private String reportCreatedAt;  // 신고 생성일
    private String reportReason;
    private String memberId;
    private String reportStatus;
//    private String messageCreatedAt; // 신고 메시지 작성 날짜


}
