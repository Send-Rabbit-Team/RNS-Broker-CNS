package shop.rns.kakaobroker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReceiveMessageDto {
    private KakaoMessageDto kakaoMessageDto;
    private KakaoMessageResultDto kakaoMessageResultDto;
}
