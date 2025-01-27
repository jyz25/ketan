package com.ketan.service.notify.service.impl;

import com.ketan.api.model.enums.NotifyTypeEnum;
import com.ketan.core.common.CommonConstants;
import com.ketan.core.rabbitmq.RabbitmqConnection;
import com.ketan.core.rabbitmq.RabbitmqConnectionPool;
import com.ketan.core.util.JsonUtil;
import com.ketan.core.util.SpringUtil;
import com.ketan.service.notify.service.NotifyService;
import com.ketan.service.notify.service.RabbitmqService;
import com.ketan.service.user.repository.entity.UserFootDO;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitmqServiceImpl implements RabbitmqService {

    @Autowired
    private NotifyService notifyService;

    @Override
    public boolean enabled() {
        return "true".equalsIgnoreCase(SpringUtil.getConfig("rabbitmq.switchFlag"));
    }

    @Override
    public void publishMsg(String exchange,
                           BuiltinExchangeType exchangeType,
                           String toutingKey,
                           String message) {
        try {
            //创建连接
            RabbitmqConnection rabbitmqConnection = RabbitmqConnectionPool.getConnection();
            Connection connection = rabbitmqConnection.getConnection();
            //创建消息通道
            Channel channel = connection.createChannel();
            // 声明exchange中的消息为可持久化，不自动删除
            channel.exchangeDeclare(exchange, exchangeType, true, false, null);
            // 发布消息
            // channel.basicPublish(exchangeName, routingKey, null, messageBodyBytes);
            channel.basicPublish(exchange, toutingKey, null, message.getBytes());
            log.info("Publish msg: {}", message);
            channel.close();
            RabbitmqConnectionPool.returnConnection(rabbitmqConnection);
        } catch (InterruptedException | IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void consumerMsg(String exchange,
                            String queueName,
                            String routingKey) {

        try {
            //创建连接
            RabbitmqConnection rabbitmqConnection = RabbitmqConnectionPool.getConnection();
            Connection connection = rabbitmqConnection.getConnection();
            //创建消息信道
            final Channel channel = connection.createChannel();
            //消息队列
            channel.queueDeclare(queueName, true, false, false, null);
            //绑定队列到交换机
            channel.queueBind(queueName, exchange, routingKey);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    log.info("Consumer msg: {}", message);

                    // 获取Rabbitmq消息，并保存到DB
                    // 说明：这里仅作为示例，如果有多种类型的消息，可以根据消息判定，简单的用 if...else 处理，复杂的用工厂 + 策略模式
                    notifyService.saveArticleNotify(JsonUtil.toObj(message, UserFootDO.class), NotifyTypeEnum.PRAISE);

                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            // 取消自动ack
            // basicConsume(String queue 消费的队列名称, boolean autoAck 是否自动确认消息, Consumer callback 处理消息的逻辑)
            channel.basicConsume(queueName, false, consumer);

            // 将连接放回连接池中
            RabbitmqConnectionPool.returnConnection(rabbitmqConnection);

            // 提供给异步线程足够的时间去处理消息
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            channel.close();

        } catch (InterruptedException | IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processConsumerMsg() {
        log.info("Begin to processConsumerMsg.");

        Integer stepTotal = 1;
        Integer step = 0;

        // TODO: 这种方式非常 Low，后续会改造成阻塞 I/O 模式
        while (true) {
            step++;
            try {
                log.info("processConsumerMsg cycle.");
                consumerMsg(CommonConstants.EXCHANGE_NAME_DIRECT, CommonConstants.QUERE_NAME_PRAISE,
                        CommonConstants.QUERE_KEY_PRAISE);
                if (step.equals(stepTotal)) {
                    Thread.sleep(10000);
                    step = 0;
                }
            } catch (Exception e) {

            }
        }
    }
}
