package com.ssginc.showpinglive.dto.object;

import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.entity.Product;
import com.ssginc.showpinglive.entity.Stream;
import com.ssginc.showpinglive.entity.StreamStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CreateStreamDto {

    private Member member;

    private Product product;

    private String streamTitle;

    private String streamDescription;

    private StreamStatus streamStatus;

    private LocalDateTime streamEnrollTime;

    public Stream toEntity() {
        return Stream.builder()
                .member(member)
                .product(product)
                .streamTitle(streamTitle)
                .streamDescription(streamDescription)
                .streamStatus(streamStatus)
                .streamEnrollTime(streamEnrollTime)
                .build();
    }
}
