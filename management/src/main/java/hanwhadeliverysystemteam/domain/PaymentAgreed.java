package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class PaymentAgreed extends AbstractEvent {

    private Long id;
    private Integer orderId;
    private String paymentStatus;
    private Double price;
   
    // keep

}
