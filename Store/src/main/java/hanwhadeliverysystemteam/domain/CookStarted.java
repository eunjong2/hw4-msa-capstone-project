package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class CookStarted extends AbstractEvent {

    private Long id;
    private String menuName;
    private Integer qty;
    private Integer cookStatus;
    private Integer orderId;
    private String orderStatus;

    public CookStarted(Menu aggregate) {
        super(aggregate);
    }

    public CookStarted() {
        super();
    }
    // keep

}
