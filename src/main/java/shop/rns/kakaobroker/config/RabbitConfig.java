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
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // Exchange
    @Bean
    public DirectExchange kakaoWorkExchange() {
        return new DirectExchange(WORK_EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange kakaoWaitExchange(){ return new DirectExchange(WAIT_EXCHANGE_NAME); }

    // Queue
    // work queue
    @Bean
    public Queue kakaoWorkCNSQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange",WAIT_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key",CNS_WAIT_ROUTING_KEY);
        args.put("x-message-ttl", WORK_TTL);
        return new Queue(CNS_WORK_QUEUE_NAME,true, false, false, args);
    }

    // wait queue
    @Bean
    public Queue kakaoWaitCNSQueue(){
        Map<String,Object> args = new HashMap<>();
//        args.put("x-message-ttl", WAIT_TTL);
        args.put("x-dead-letter-exchange", WORK_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", CNS_WORK_ROUTING_KEY);

        return new Queue(CNS_WAIT_QUEUE_NAME,true, false, false, args);
    }

    // binding
    @Bean
    public Binding bindingKakaoWorkCNS(DirectExchange kakaoWorkExchange, Queue kakaoWorkCNSQueue){
        return BindingBuilder.bind(kakaoWorkCNSQueue)
                .to(kakaoWorkExchange)
                .with(CNS_WORK_ROUTING_KEY);
    }

    @Bean
    public Binding bindingKakaoWaitCNS(DirectExchange kakaoWaitExchange, Queue kakaoWaitCNSQueue){
        return BindingBuilder.bind(kakaoWaitCNSQueue)
                .to(kakaoWaitExchange)
                .with(CNS_WAIT_ROUTING_KEY);
    }
}
