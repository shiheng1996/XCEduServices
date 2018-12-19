package com.xuecheng.test.Rabbtimq;

import com.xuecheng.test.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitmqTopic_BootTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

      @Test
    public void send1(){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("email","发邮件啊");
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,RabbitmqConfig.ROUTINGKEY_SMS,map);
    }

    @Test
    public void sendString(){
       String  email="send email to user";
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,RabbitmqConfig.ROUTINGKEY_EMAIL,email);
    }




}
