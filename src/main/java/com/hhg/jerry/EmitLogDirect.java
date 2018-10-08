package com.hhg.jerry;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogDirect {

    private static final String EXCHANGE_NAME = "direct_logs";
    private static final String[] routingKeys = new String[]{"info" ,"warning", "error"};
    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String message = "log ";
        for(int i=0;i<10;i++){
            String routingKey = routingKeys[i % routingKeys.length];
            String msg = message + i + " level ：" + routingKey;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
            System.out.println(" [x] Sent '" + msg + "'");
        }
        channel.close();
        connection.close();
    }
}
