package shop.rns.kakaobroker.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static shop.rns.kakaobroker.utils.rabbitmq.RabbitUtil.*;

@Configuration
public class RabbitConfig {

    // Json Converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Rabbit Template
    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // Exchange
    @Bean
    public DirectExchange kakaoWorkExchange() {
        return new DirectExchange(KAKAO_EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange kakaoReceiveExchange(){ return new DirectExchange(RECEIVE_EXCHANGE_NAME); }

    @Bean
    public DirectExchange dlxKakaoExchange(){ return new DirectExchange(DLX_EXCHANGE_NAME); }

    @Bean
    public DirectExchange DeadKakaoExchange(){ return new DirectExchange(DEAD_EXCHANGE_NAME); }

    // Queue
    // work queue
    @Bean
    public Queue kakaoWorkCNSQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange",DLX_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key",CNS_WAIT_ROUTING_KEY);
        return new Queue(CNS_WORK_QUEUE_NAME,true, false, false, args);
    }

    // receive queue
    @Bean
    public Queue kakaoReceiveCNSQueue(){
        return new Queue(CNS_RECEIVE_QUEUE_NAME, true);
    }

    // wait queue
    @Bean
    public Queue kakaoWaitCNSQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-message-ttl", WAIT_TTL);
        args.put("x-dead-letter-exchange",KAKAO_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key",CNS_WORK_ROUTING_KEY);

        return new Queue(CNS_WAIT_QUEUE_NAME,true, false, false, args);
    }

    // binding
    // work exchange + work queue + work routing key
    @Bean
    public Binding bindingKakaoCNS(DirectExchange kakaoWorkExchange, Queue kakaoWorkCNSQueue){
        return BindingBuilder.bind(kakaoWorkCNSQueue)
                .to(kakaoWorkExchange)
                .with(CNS_WORK_ROUTING_KEY);
    }

    // receive exchange + receive queue + receive routing key
    @Bean
    public Binding bindingKakaoReceiveKE(DirectExchange kakaoReceiveExchange, Queue kakaoReceiveKEQueue){
        return BindingBuilder.bind(kakaoReceiveKEQueue)
                .to(kakaoReceiveExchange)
                .with(CNS_RECEIVE_ROUTING_KEY);
    }

    // dlx exchange + dlx queue + dlx routing key
    @Bean
    public Binding bindingKakaoDlxKE(DirectExchange kakaoDlxExchange, Queue kakaoWaitKEQueue){
        return BindingBuilder.bind(kakaoWaitKEQueue)
                .to(kakaoDlxExchange)
                .with(CNS_WAIT_ROUTING_KEY);
    }
}
