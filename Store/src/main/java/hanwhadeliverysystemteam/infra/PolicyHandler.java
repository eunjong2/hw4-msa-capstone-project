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
    MenuRepository menuRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PaymentAgreed'"
    )
    public void wheneverPaymentAgreed_AddOrderList(
        @Payload PaymentAgreed paymentAgreed
    ) {
        PaymentAgreed event = paymentAgreed;
        System.out.println(
            "\n\n##### listener AddOrderList : " + paymentAgreed + "\n\n"
        );

        // Sample Logic //
        Menu.addOrderList(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PaymentCancelled'"
    )
    public void wheneverPaymentCancelled_Cancel(
        @Payload PaymentCancelled paymentCancelled
    ) {
        PaymentCancelled event = paymentCancelled;
        System.out.println(
            "\n\n##### listener Cancel : " + paymentCancelled + "\n\n"
        );

        // Sample Logic //
        Menu.cancel(event);
    }
    // keep

}
