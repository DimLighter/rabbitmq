package com.hhg.jerry.workers;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by lining on 2018/10/8.
 */
public class Work2 {
    private static final String TASK_QUEUE_NAME = "tasks";
    static Logger logger = LoggerFactory.getLogger(Work2.class);
    public static void main(String[] args) throws IOException, TimeoutException {
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
        logger.info("Worker2 is waiting for task");

        channel.basicQos(2);

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                logger.info("Worker2 is handle task:'" + message + "'");
                try {
                    Thread.sleep(800);
                }catch (Exception e){
                    channel.abort();
                }finally {
                    channel.basicAck(envelope.getDeliveryTag(),false);
                    logger.info("Worker2 finish task:'" + message + "'");
                }
            }
        };
        boolean autoAck=false;
        //消息消费完成确认
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, consumer);
    }
}
