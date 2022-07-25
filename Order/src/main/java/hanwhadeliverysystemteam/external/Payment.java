package hanwhadeliverysystemteam.external;

import java.util.Date;
import lombok.Data;

@Data
public class Payment {

    private Long id;
    private Integer orderId;
    private String paymentStatus;
    private Double price;
    // keep

}
