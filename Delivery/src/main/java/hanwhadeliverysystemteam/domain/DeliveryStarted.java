package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class DeliveryStarted extends AbstractEvent {

    private Long id;
    private String orderStatus;
    private Long orderId;
    private Integer qty;
    private Double price;
    private String address;
    private String deliveryStatus;
    private String menuName;

    public DeliveryStarted(Delivery aggregate) {
        super(aggregate);
    }

    public DeliveryStarted() {
        super();
    }
    // keep

}
