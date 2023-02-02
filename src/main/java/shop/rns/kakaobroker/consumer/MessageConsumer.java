package shop.rns.kakaobroker.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import shop.rns.kakaobroker.config.status.MessageStatus;
import shop.rns.kakaobroker.dlx.DlxProcessingErrorHandler;
import shop.rns.kakaobroker.dto.KakaoMessageDto;
import shop.rns.kakaobroker.dto.KakaoMessageResultDto;
import shop.rns.kakaobroker.dto.ReceiveMessageDto;

import java.io.IOException;

import static shop.rns.kakaobroker.utils.rabbitmq.RabbitUtil.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class MessageConsumer {
    private final DlxProcessingErrorHandler dlxProcessingErrorHandler;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues="q.kakao.cns.work",concurrency = "3", ackMode = "MANUAL")
    public void receiveMessage(Message message, Channel channel){
        try{
            // ReceiveMessageDTO 변환
            ReceiveMessageDto receiveMessageDto = objectMapper.readValue(new String(message.getBody()), ReceiveMessageDto.class);

            // KakaoMessageDTO 추출
            KakaoMessageDto kakaoMessageDto = receiveMessageDto.getKakaoMessageDto();
            System.out.println("메시지 내용 = " + kakaoMessageDto.getContent());

            // MessageResultDTO 추출
            KakaoMessageResultDto kakaoMessageResultDto = receiveMessageDto.getKakaoMessageResultDto();


            // 수신 완료 ack 발송
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            sendResponseToSendServer(kakaoMessageResultDto);

        } catch (IOException e){
            // DLX 에러 핸들링
            log.warn("Error processing message:" + message.getBody().toString() + ":" + e.getMessage());
            dlxProcessingErrorHandler.handleErrorProcessingMessage(message, channel);

        }
    }

    public void sendResponseToSendServer(KakaoMessageResultDto kakaoMessageResultDto){
        changeMessageStatusSuccess(kakaoMessageResultDto);

        rabbitTemplate.convertAndSend(RECEIVE_EXCHANGE_NAME,CNS_RECEIVE_ROUTING_KEY, kakaoMessageResultDto);
    }

    public KakaoMessageResultDto changeMessageStatusSuccess(KakaoMessageResultDto kakaoMessageResultDto){
        kakaoMessageResultDto.setMessageStatus(MessageStatus.SUCCESS);
        return kakaoMessageResultDto;
    }
}
