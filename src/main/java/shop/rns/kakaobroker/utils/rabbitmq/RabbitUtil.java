package shop.rns.kakaobroker.utils.rabbitmq;

public class RabbitUtil {

    // Exchange
    public static final String WORK_EXCHANGE_NAME = "dx.kakao.work";
    public static final String WAIT_EXCHANGE_NAME = "dx.kakao.wait";
    public static final String RECEIVE_EXCHANGE_NAME = "dx.kakao.receive";

    // Queue
    public static final String CNS_WORK_QUEUE_NAME = "q.kakao.cns.work";
    public static final String CNS_WAIT_QUEUE_NAME = "q.kakao.cns.wait";
    public static final String CNS_RECEIVE_QUEUE_NAME = "q.kakao.cns.receive";

    // Routing Key
    public static final String CNS_WORK_ROUTING_KEY = "kakao.work.cns";
    public static final String CNS_WAIT_ROUTING_KEY = "kakao.wait.cns";
    public static final String CNS_RECEIVE_ROUTING_KEY = "kakao.receive.cns";
    public static final String CNS_SENDER_ROUTING_KEY = "kakao.sender.cns";


    // TTL
    public static final int WORK_TTL = 5 * 1000;
}
