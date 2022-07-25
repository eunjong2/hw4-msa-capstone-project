package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class PaymentCancelled extends AbstractEvent {

    private Long id;
    private Long orderId;

    private String paymentStatus;

    private Double price;

    private String menuName;

    private Integer qty;
    private String address;

    public PaymentCancelled(Payment aggregate) {
        super(aggregate);
    }

    public PaymentCancelled() {
        super();
    }
    // keep

}
