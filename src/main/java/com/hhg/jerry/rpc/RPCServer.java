package com.hhg.jerry.rpc;

/**
 * Created by lining on 2018/10/8.
 */
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";
    static Logger logger = LoggerFactory.getLogger(RPCServer.class);
    private static int fib(int n) {
        return n+100;
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = null;
        connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        channel.queuePurge(RPC_QUEUE_NAME);

        channel.basicQos(1);

        logger.info("Server is waiting RPC requests");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();
                try {
                    String message = new String(body,"UTF-8");
                    int n = Integer.parseInt(message);
                    String response = fib(n) + "";
                    channel.basicPublish( "", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
                finally {
                    synchronized(this) {
                        this.notify();
                    }
                }
            }
        };

        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
        // Wait and be prepared to consume the message from RPC client.
        while (true) {
            synchronized(consumer) {
                try {
                    consumer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
