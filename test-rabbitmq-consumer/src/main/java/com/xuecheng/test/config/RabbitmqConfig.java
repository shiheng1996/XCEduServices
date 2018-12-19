package com.xuecheng.test.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    //队列
    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //交换机
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    //包含通配符的routing key
    public static final String ROUTINGKEY_EMAIL="inform.aaa.email.bbb";
    public static final String ROUTINGKEY_SMS="inform.aaa.sms.bbb";

//    /**
//     * 实例化 topic类型的交换机
//     * @return
//     */
//    @Bean(EXCHANGE_TOPICS_INFORM)
//    public Exchange  buildExchange(){
//        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
//    }
    /**
     * 实例化队列
     */
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue  QUEUE_INFORM_EMAIL(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }
    @Bean(QUEUE_INFORM_SMS)
    public Queue  QUEUE_INFORM_SMS(){
        return new Queue(QUEUE_INFORM_SMS);
    }

//    /**
//     * 将队列和交换机绑定 并且指定routingKey  ROUTINGKEY_EMAIL
//     * @param queue
//     * @param exchange
//     * @return
//     */
//    @Bean
//    public Binding  BINDING_QUEUE_INFORM_EMAIL(@Qualifier(QUEUE_INFORM_EMAIL) Queue queue,@Qualifier(EXCHANGE_TOPICS_INFORM)Exchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_EMAIL).noargs();
//    }
//
//    /**
//     *  将队列和交换机绑定并指定routingkey  ROUTINGKEY_SMS
//     * @param queue
//     * @param exchange
//     * @return
//     */
//    @Bean
//    public Binding  BINDING_QUEUE_INFORM_SMS(@Qualifier(QUEUE_INFORM_SMS) Queue queue,@Qualifier(EXCHANGE_TOPICS_INFORM)Exchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_SMS).noargs();
//    }
}
