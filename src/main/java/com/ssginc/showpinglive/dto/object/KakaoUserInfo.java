package com.ssginc.showpinglive.dto.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoUserInfo {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    private Properties properties;

    @Data
    public static class KakaoAccount {
        private String email;
    }

    @Data
    public static class Properties {
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;
    }
}
