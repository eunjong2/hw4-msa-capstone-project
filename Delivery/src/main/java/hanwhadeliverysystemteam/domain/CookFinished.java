package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.domain.*;
import hanwhadeliverysystemteam.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class CookFinished extends AbstractEvent {

    private Long id;
    private String menuName;
    private Integer qty;
    private String cookStatus;
    private Long orderId;
    private Double price;
    private String address;



    // keep

}
