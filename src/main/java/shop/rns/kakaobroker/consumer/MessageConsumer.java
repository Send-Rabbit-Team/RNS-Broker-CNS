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
import shop.rns.kakaobroker.dlx.RabbitmqHeader;
import shop.rns.kakaobroker.dto.KakaoMessageDto;
import shop.rns.kakaobroker.dto.KakaoMessageResultDto;
import shop.rns.kakaobroker.dto.ReceiveMessageDto;

import java.io.IOException;

import static shop.rns.kakaobroker.utils.rabbitmq.RabbitUtil.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class MessageConsumer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues="q.kakao.cns.work",concurrency = "3", ackMode = "MANUAL")
    public void receiveMessage(Message message, Channel channel) throws IOException {
        try{
            // ReceiveMessageDTO 변환
            ReceiveMessageDto receiveMessageDto = objectMapper.readValue(new String(message.getBody()), ReceiveMessageDto.class);

            KakaoMessageDto kakaoMessageDto = receiveMessageDto.getKakaoMessageDto();
            System.out.println("메시지 내용 = " + kakaoMessageDto.getContent());

            KakaoMessageResultDto kakaoMessageResultDto = receiveMessageDto.getKakaoMessageResultDto();

            RabbitmqHeader rabbitmqHeader = new RabbitmqHeader(message.getMessageProperties().getHeaders());
            long retryCount = rabbitmqHeader.getFailedRetryCount();

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            sendResponseToSendServer(kakaoMessageResultDto, retryCount);

//            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e){
            // DLX 에러 핸들링
            log.warn("Error processing message:" + message.getBody().toString() + ":" + e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    public void sendResponseToSendServer(KakaoMessageResultDto kakaoMessageResultDto, long retryCount){
        changeMessageStatusSuccess(kakaoMessageResultDto);
        kakaoMessageResultDto.setRetryCount(retryCount);

        rabbitTemplate.convertAndSend(RECEIVE_EXCHANGE_NAME, CNS_RECEIVE_ROUTING_KEY, kakaoMessageResultDto);
        log.info("response to sender server: {}", kakaoMessageResultDto.getMessageId());
    }

    public KakaoMessageResultDto changeMessageStatusSuccess(KakaoMessageResultDto kakaoMessageResultDto){
        kakaoMessageResultDto.setMessageStatus(MessageStatus.SUCCESS);
        return kakaoMessageResultDto;
    }
}
