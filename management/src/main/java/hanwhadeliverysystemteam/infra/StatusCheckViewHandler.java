package hanwhadeliverysystemteam.infra;

import hanwhadeliverysystemteam.config.kafka.KafkaProcessor;
import hanwhadeliverysystemteam.domain.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class StatusCheckViewHandler {

    @Autowired
    private StatusCheckRepository statusCheckRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenMenuOrdered_then_CREATE_1(
        @Payload MenuOrdered menuOrdered
    ) {
        try {
            if (!menuOrdered.validate()) return;

            // view 객체 생성
            StatusCheck statusCheck = new StatusCheck();
            // view 객체에 이벤트의 Value 를 set 함
            statusCheck.setId(menuOrdered.getId());
            statusCheck.setStatus("Ordered");
            // view 레파지 토리에 save
            statusCheckRepository.save(statusCheck);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCookStarted_then_UPDATE_1(
        @Payload CookStarted cookStarted
    ) {
        try {
            if (!cookStarted.validate()) return;
            // view 객체 조회

            List<StatusCheck> statusCheckList = statusCheckRepository.findByOrderId(
                cookStarted.getOrderId()
            );
            for (StatusCheck statusCheck : statusCheckList) {
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                statusCheck.setStatus("CookStarted");
                // view 레파지 토리에 save
                statusCheckRepository.save(statusCheck);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCookFinished_then_UPDATE_2(
        @Payload CookFinished cookFinished
    ) {
        try {
            if (!cookFinished.validate()) return;
            // view 객체 조회

            List<StatusCheck> statusCheckList = statusCheckRepository.findByOrderId(
                cookFinished.getOrderId()
            );
            for (StatusCheck statusCheck : statusCheckList) {
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                statusCheck.setStatus("CookFinished");
                // view 레파지 토리에 save
                statusCheckRepository.save(statusCheck);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenMenuCancelled_then_UPDATE_3(
        @Payload MenuCancelled menuCancelled
    ) {
        try {
            if (!menuCancelled.validate()) return;
            // view 객체 조회

            List<StatusCheck> statusCheckList = statusCheckRepository.findByOrderId(
                String.valueOf(menuCancelled.getOrderId())
            );
            for (StatusCheck statusCheck : statusCheckList) {
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                statusCheck.setStatus("MenuCancelled");
                // view 레파지 토리에 save
                statusCheckRepository.save(statusCheck);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentAgreed_then_UPDATE_4(
        @Payload PaymentAgreed paymentAgreed
    ) {
        try {
            if (!paymentAgreed.validate()) return;
            // view 객체 조회

            List<StatusCheck> statusCheckList = statusCheckRepository.findByOrderId(
                paymentAgreed.getOrderId()
            );
            for (StatusCheck statusCheck : statusCheckList) {
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                statusCheck.setStatus("PaymentAgreed");
                // view 레파지 토리에 save
                statusCheckRepository.save(statusCheck);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCancelled_then_UPDATE_5(
        @Payload PaymentCancelled paymentCancelled
    ) {
        try {
            if (!paymentCancelled.validate()) return;
            // view 객체 조회

            List<StatusCheck> statusCheckList = statusCheckRepository.findByOrderId(
                paymentCancelled.getOrderId()
            );
            for (StatusCheck statusCheck : statusCheckList) {
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                statusCheck.setStatus("PaymentCancelled");
                // view 레파지 토리에 save
                statusCheckRepository.save(statusCheck);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryStarted_then_UPDATE_6(
        @Payload DeliveryStarted deliveryStarted
    ) {
        try {
            if (!deliveryStarted.validate()) return;
            // view 객체 조회

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryCompleted_then_UPDATE_7(
        @Payload DeliveryCompleted deliveryCompleted
    ) {
        try {
            if (!deliveryCompleted.validate()) return;
            // view 객체 조회

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // keep

}
