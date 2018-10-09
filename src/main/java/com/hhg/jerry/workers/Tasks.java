package com.hhg.jerry.workers;

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
public class Tasks {
    public final static String QUEUE_NAME="tasks";
    static Logger logger = LoggerFactory.getLogger(Tasks.class);
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        for(int i=0;i<100;i++){
            String message = "task " + i ;
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            logger.info("create task: +'" + message + "'");
        }
        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
