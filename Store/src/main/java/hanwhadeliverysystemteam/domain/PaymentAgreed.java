package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class PaymentAgreed extends AbstractEvent {

    private Long id;
    private String orderId;
    private String paymentStatus;
    private Double price;
    private Integer orderId;
    // keep

}
