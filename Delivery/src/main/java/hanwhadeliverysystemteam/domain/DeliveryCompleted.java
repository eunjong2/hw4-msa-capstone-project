package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class DeliveryCompleted extends AbstractEvent {

    private Long id;
    private String orderStatus;
    private Long orderId;
    private Integer qty;
    private Double price;
    private String address;
    private String deliveryStatus;
    private String menuName;
    public DeliveryCompleted(Delivery aggregate) {
        super(aggregate);
    }

    public DeliveryCompleted() {
        super();
    }
    // keep

}
