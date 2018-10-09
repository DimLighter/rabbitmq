package com.hhg.jerry.pubsub.topic;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LazyOrOrangeTopic {
    private static final String EXCHANGE_NAME = "topic_logs";
    static Logger logger = LoggerFactory.getLogger(LazyOrOrangeTopic.class);
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //声明一个匹配模式的交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();
        //路由关键字
        String[] routingKeys = new String[]{"*.orange.*","lazy.#"};
        //绑定路由
        for (String routingKey : routingKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        }
        logger.info("Waiting for messages");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                logger.info("Received msg, key is {} and msg is {}",envelope.getRoutingKey(),message);
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
