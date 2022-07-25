package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class DeliveryCompleted extends AbstractEvent {

    private Long id;
    private Integer orderId;
    private String orderAddress;
    private String shopAddress;
    private String orderStatus;
    private String deliveryStatus;

    public DeliveryCompleted(Delivery aggregate) {
        super(aggregate);
    }

    public DeliveryCompleted() {
        super();
    }
    // keep

}
