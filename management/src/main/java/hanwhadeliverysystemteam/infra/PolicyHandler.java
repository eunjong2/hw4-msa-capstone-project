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

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='MenuCancelled'"
    )
    public void wheneverMenuCancelled_NotifyAlarm(
        @Payload MenuCancelled menuCancelled
    ) {
        MenuCancelled event = menuCancelled;
        System.out.println(
            "\n\n##### listener NotifyAlarm : " + menuCancelled + "\n\n"
        );
        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='MenuOrdered'"
    )
    public void wheneverMenuOrdered_NotifyAlarm(
        @Payload MenuOrdered menuOrdered
    ) {
        MenuOrdered event = menuOrdered;
        System.out.println(
            "\n\n##### listener NotifyAlarm : " + menuOrdered + "\n\n"
        );
        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='DeliveryCompleted'"
    )
    public void wheneverDeliveryCompleted_NotifyAlarm(
        @Payload DeliveryCompleted deliveryCompleted
    ) {
        DeliveryCompleted event = deliveryCompleted;
        System.out.println(
            "\n\n##### listener NotifyAlarm : " + deliveryCompleted + "\n\n"
        );
        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='DeliveryStarted'"
    )
    public void wheneverDeliveryStarted_NotifyAlarm(
        @Payload DeliveryStarted deliveryStarted
    ) {
        DeliveryStarted event = deliveryStarted;
        System.out.println(
            "\n\n##### listener NotifyAlarm : " + deliveryStarted + "\n\n"
        );
        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='CookFinished'"
    )
    public void wheneverCookFinished_NotifyAlarm(
        @Payload CookFinished cookFinished
    ) {
        CookFinished event = cookFinished;
        System.out.println(
            "\n\n##### listener NotifyAlarm : " + cookFinished + "\n\n"
        );
        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='CookStarted'"
    )
    public void wheneverCookStarted_NotifyAlarm(
        @Payload CookStarted cookStarted
    ) {
        CookStarted event = cookStarted;
        System.out.println(
            "\n\n##### listener NotifyAlarm : " + cookStarted + "\n\n"
        );
        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PaymentCancelled'"
    )
    public void wheneverPaymentCancelled_NotifyAlarm(
        @Payload PaymentCancelled paymentCancelled
    ) {
        PaymentCancelled event = paymentCancelled;
        System.out.println(
            "\n\n##### listener NotifyAlarm : " + paymentCancelled + "\n\n"
        );
        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PaymentAgreed'"
    )
    public void wheneverPaymentAgreed_NotifyAlarm(
        @Payload PaymentAgreed paymentAgreed
    ) {
        PaymentAgreed event = paymentAgreed;
        System.out.println(
            "\n\n##### listener NotifyAlarm : " + paymentAgreed + "\n\n"
        );
        // Sample Logic //

    }
    // keep

}
