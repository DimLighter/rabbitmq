package com.hhg.jerry.pubsub.direct;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectLog {

    private static final String EXCHANGE_NAME = "direct_logs";
    private static Logger logger = LoggerFactory.getLogger(DirectLog.class);
    private static final String[] routingKeys = new String[]{"info" ,"warn", "error"};
    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String message = "log ";
        for(int i=0;i<10;i++){
            String routingKey = routingKeys[i % routingKeys.length];
            String msg = message + i + " with level ：" + routingKey;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
            logger.info("produce log:'" + msg + "'");
        }
        channel.close();
        connection.close();
    }
}
