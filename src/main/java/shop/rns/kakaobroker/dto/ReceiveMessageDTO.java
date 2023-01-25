package shop.rns.kakaobroker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReceiveMessageDTO {
    private KakaoMessageDTO kakaoMessageDTO;
    private MessageResultDTO messageResultDTO;
}
