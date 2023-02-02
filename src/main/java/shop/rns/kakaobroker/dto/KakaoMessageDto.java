package shop.rns.kakaobroker.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import shop.rns.kakaobroker.config.status.MessageStatus;
import shop.rns.kakaobroker.config.type.ButtonType;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KakaoMessageDto {
    private String from;

    @JsonIgnore
    private String to;

    private String title;

    private String subtitle;

    private String content;

    private String description;

    private String image;

    private MessageStatus messageStatus;

    @JsonIgnore
    private String reserveTime;

    @JsonIgnore
    private String scheduleCode;

    private String buttonTitle;

    private String buttonUrl;

    private ButtonType buttonType;
}
