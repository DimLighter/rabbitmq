package com.hhg.jerry.pubsub.direct;


import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WarnLog {
    private static final String EXCHANGE_NAME = "direct_logs";
    private static Logger logger = LoggerFactory.getLogger(WarnLog.class);
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "warn");

        logger.info("WarnLog processor is waiting for log");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                logger.info(" receive log:" + message);
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
