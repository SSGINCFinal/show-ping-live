package com.ssginc.showpinglive.api;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author dckat
 * segment별 자막정보를 저장한 클래스
 * <p>
 */
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Segments {

    private Long start;     // 시작 시간
    private Long end;       // 끝 시간
    private String text;    // 자막 내용

}
