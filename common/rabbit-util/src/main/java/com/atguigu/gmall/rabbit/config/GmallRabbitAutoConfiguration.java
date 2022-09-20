package com.atguigu.gmall.rabbit.config;

import com.atguigu.gmall.rabbit.service.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author Connor
 * @date 2022/9/15
 */
@Configuration
@EnableRabbit
@Slf4j
public class GmallRabbitAutoConfiguration {

    @Bean
    public RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer,
                                         ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        //消息投递到交换机时调用
        template.setConfirmCallback((CorrelationData correlationData,
                                     boolean ack,
                                     String cause)->{
            if (!ack) {
                log.error("消息投递到服务器失败，保存到数据库,消息：{}",correlationData);
                // TODO: 2022/9/16 消息投递到服务器失败，保存到数据库 
            }
        });
        //消息从交换机投递队列失败时调用
        template.setReturnCallback((Message message,
                                    int replyCode,
                                    String replyText,
                                    String exchange,
                                    String routingKey)->{
            //消息没有被正确投递到队列
            log.error("消息投递到队列失败，保存到数据库,{}",message);
            // TODO: 2022/9/16 消息投递到队列失败，保存到数据库
        });
        //设置重试器 投递失败会默认重试3次
        template.setRetryTemplate(new RetryTemplate());

        return template;
    }

    @Bean
    public MqService mqService() {
        return new MqService();
    }
}
