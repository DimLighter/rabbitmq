package com.hhg.jerry.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by lining on 2018/10/8.
 */
public class Sender {
    public final static String QUEUE_NAME="simple";
    static Logger logger = LoggerFactory.getLogger(Sender.class);
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        int i = 1;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        while (i<10){
            String message = "msg " + i++;
            //发送消息到队列中
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            logger.info("Send msg : {}", message);
//            Thread.sleep(200);
        }
        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
