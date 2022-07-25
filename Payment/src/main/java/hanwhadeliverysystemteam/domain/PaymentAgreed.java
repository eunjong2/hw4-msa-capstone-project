package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class PaymentAgreed extends AbstractEvent {

    private Long id;
    private Long orderId;

    private String paymentStatus;

    private Double price;

    private String menuName;

    private Integer qty;
    private String address;
    public PaymentAgreed(Payment aggregate) {
        super(aggregate);
    }

    public PaymentAgreed() {
        super();
    }
    // keep

}
