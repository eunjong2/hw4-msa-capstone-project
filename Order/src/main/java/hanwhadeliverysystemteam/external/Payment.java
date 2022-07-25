package hanwhadeliverysystemteam.external;

import java.util.Date;
import lombok.Data;

@Data
public class Payment {

    private Long id;
    private Long orderId;
    private String paymentStatus;
    private Double price;

    private String menuName;

    private Integer qty;

    private String cookStatus;
    private String address;

    
    // keep

}
