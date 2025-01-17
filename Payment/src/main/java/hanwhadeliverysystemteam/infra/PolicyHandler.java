package hanwhadeliverysystemteam.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import hanwhadeliverysystemteam.config.kafka.KafkaProcessor;
import hanwhadeliverysystemteam.domain.*;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PolicyHandler {

    @Autowired
    PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='MenuCancelled'"
    )
    public void wheneverMenuCancelled_CancelPayment(
        @Payload MenuCancelled menuCancelled
    ) {
        MenuCancelled event = menuCancelled;
        System.out.println(
            "\n\n##### listener CancelPayment : " + menuCancelled + "\n\n"
        );
        String orderStatus = event.getOrderStatus();
        if(!"Accepted".equals(orderStatus) || "Finished".equals(orderStatus)){
            Payment.cancelPayment(event);
        }
    }


}
