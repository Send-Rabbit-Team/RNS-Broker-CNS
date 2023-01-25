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
import shop.rns.kakaobroker.dto.KakaoMessageDTO;
import shop.rns.kakaobroker.dto.MessageResultDTO;
import shop.rns.kakaobroker.dto.ReceiveMessageDTO;

import java.io.IOException;

import static shop.rns.kakaobroker.utils.rabbitmq.RabbitUtil.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues="q.kakao.ke.work",concurrency = "3", ackMode = "MANUAL")
    public void receiveMessage(Message message, Channel channel){
        try{
            // ReceiveMessageDTO 변환
            ReceiveMessageDTO receiveMessageDTO = objectMapper.readValue(new String(message.getBody()),ReceiveMessageDTO.class);

            // KakaoMessageDTO 추출
            KakaoMessageDTO kakaoMessageDTO = receiveMessageDTO.getKakaoMessageDTO();
            System.out.println("메시지 내용 = " + kakaoMessageDTO.getContent());

            // MessageResultDTO 추출
            MessageResultDTO messageResultDTO = receiveMessageDTO.getMessageResultDTO();

            sendResponseToSendServer(messageResultDTO);

            // 수신 완료 ack 발송
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (IOException e){
            // DLX 에러 핸들링
            log.warn("Error processing message:" + message.getBody().toString() + ":" + e.getMessage());
        }
    }

    public void sendResponseToSendServer(MessageResultDTO messageResultDTO){
        changeMessageStatusSuccess(messageResultDTO);

        rabbitTemplate.convertAndSend(RECEIVE_EXCHANGE_NAME,CNS_RECEIVE_ROUTING_KEY,messageResultDTO);
    }

    public MessageResultDTO changeMessageStatusSuccess(MessageResultDTO messageResultDTO){
        messageResultDTO.setMessageStatus(MessageStatus.SUCCESS);
        return messageResultDTO;
    }
}
