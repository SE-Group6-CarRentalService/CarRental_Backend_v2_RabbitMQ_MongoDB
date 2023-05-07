package at.fhcampuswien.carrental.carrentalservice.AMQP;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Runner {

    private final RabbitTemplate rabbitTemplate;
    private final Receiver receiver;

    public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
        this.receiver = receiver;
        this.rabbitTemplate = rabbitTemplate;
    }

    //public void sendCustomerRabbitMqMessage(CustomerAttribute customerAttribute) throws Exception {
    public void sendCustomerRabbitMqMessage() throws Exception {
        System.out.println("Sending message...");
        //rabbitTemplate.convertAndSend(AmqpConfiguration.topicExchangeName, "foo.bar.baz", customerAttribute);
        rabbitTemplate.convertAndSend(AmqpConfiguration.topicExchangeName, "foo.bar.baz", "Test_Message");
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }


}
