package shop.rns.kakaobroker.dto;

import lombok.*;
import shop.rns.kakaobroker.config.status.MessageStatus;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KakaoMessageResultDto {
    private String rMessageResultId;

    private long messageId;

    private long brokerId;

    private long contactId;

    private MessageStatus messageStatus;

    private long retryCount;

    private LocalDateTime createdAt;
}
