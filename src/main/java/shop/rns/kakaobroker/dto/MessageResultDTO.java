package shop.rns.kakaobroker.dto;

import lombok.*;
import shop.rns.kakaobroker.config.status.MessageStatus;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageResultDTO {
    private long messageId;
    private long brokerId;
    private long contactId;
    private MessageStatus messageStatus;
    private LocalDateTime createdAt;
}
