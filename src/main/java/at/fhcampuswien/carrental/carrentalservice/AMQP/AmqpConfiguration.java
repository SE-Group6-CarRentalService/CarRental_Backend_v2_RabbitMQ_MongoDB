package at.fhcampuswien.carrental.carrentalservice.AMQP;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {
    public static final String topicExchangeName = "carRental.rpc";

    static final String queueName = "carRental.rpc.requests";

    //@Bean
    //Queue queue() {
    //    return new Queue(queueName, false);
    //}

    /*
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }
     */

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(topicExchangeName);
    }


    //added last

    @Bean
        //Queue queue() { return new Queue(queueName, false);
    Queue queue() { return new Queue(queueName, true);
    }

    @Bean
    Binding binding(DirectExchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("rpc");
    }

    //added last end




    //@Bean
    //public CustomerController client() {
    //    return new CustomerController();
    //}

    /*
    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("rpc");
    }

     */

    /*
    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }
     */

    /*
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

     */
}
