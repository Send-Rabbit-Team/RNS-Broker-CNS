package shop.rns.kakaobroker.utils.rabbitmq;

public class RabbitUtil {

    // Exchange
    public static final String KAKAO_EXCHANGE_NAME = "dx.kakao.work";
    public static final String DLX_EXCHANGE_NAME = "dx.kakao.wait";
    public static final String DEAD_EXCHANGE_NAME = "dx.kakao.dead";
    public static final String RECEIVE_EXCHANGE_NAME = "dx.kakao.receive";

    // Queue
    public static final String CNS_WORK_QUEUE_NAME = "q.kakao.cns.work";
    public static final String CNS_WAIT_QUEUE_NAME = "q.kakao.cns.wait";
    public static final String CNS_RECEIVE_QUEUE_NAME = "q.kakao.cns.receive";

    // Routing Key
    public static final String CNS_WORK_ROUTING_KEY = "kakao.send.cns";
    public static final String CNS_WAIT_ROUTING_KEY = "kakao.wait.cns";
    public static final String CNS_RECEIVE_ROUTING_KEY = "kakao.receive.cns";

    // DLX Routing key
    public static final String KE_WAIT_ROUTING_KEY = "kakao.wait.ke";

    // TTL
    public static final int WAIT_TTL = 3000;
}
