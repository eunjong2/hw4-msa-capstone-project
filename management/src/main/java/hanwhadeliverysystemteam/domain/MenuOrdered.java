package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class MenuOrdered extends AbstractEvent {

    private Long id;
    private String menuName;
    private Integer orderId;
    private String address;
    private Integer qty;
    private String orderStatus;
    // keep

}
