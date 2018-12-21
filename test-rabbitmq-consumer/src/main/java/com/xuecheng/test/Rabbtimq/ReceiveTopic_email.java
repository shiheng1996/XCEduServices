package com.xuecheng.test.Rabbtimq;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.sql.SQLOutput;

@Component
public class ReceiveTopic_email {
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void receive(String aaa, Message message, Channel channel){
        System.out.println(aaa);
        System.out.println(message);

    }


}
