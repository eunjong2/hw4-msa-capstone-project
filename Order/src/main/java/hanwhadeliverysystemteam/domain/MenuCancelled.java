package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class MenuCancelled extends AbstractEvent {

    private Long id;
    private String menuName;
    private Long orderId;
    private String address;
    private Integer qty;
    private String orderStatus;
    private Double price;
    

    public MenuCancelled(Order aggregate) {
        super(aggregate);
    }

    public MenuCancelled() {
        super();
    }
    // keep

}
