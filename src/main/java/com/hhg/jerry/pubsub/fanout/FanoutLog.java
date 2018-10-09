package com.hhg.jerry.pubsub.fanout;


import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FanoutLog {

    private static final String EXCHANGE_NAME = "fanout-logs";
    static Logger logger = LoggerFactory.getLogger(FanoutLog.class);
    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        String message;
        for(int i=0;i<10;i++){
            message = "log" + i;
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            logger.info("Sent log : '" + message + "'");
        }
        channel.close();
        connection.close();
    }
}
