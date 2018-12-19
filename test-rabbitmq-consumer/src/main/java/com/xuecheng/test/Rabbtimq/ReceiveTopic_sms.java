package com.xuecheng.test.Rabbtimq;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReceiveTopic_sms {
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_SMS})
    public void receive(Map aaa, Message message, Channel channel){
        System.out.println(aaa);
    }


}
