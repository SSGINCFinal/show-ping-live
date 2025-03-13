package com.ssginc.showpinglive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDto {
    private Long reportNo;
    private String reportCreatedAt;  // 필요한 경우 형식화된 날짜 문자열로 반환
    private String reportReason;
    private String memberId;
    private String reportStatus;
}
