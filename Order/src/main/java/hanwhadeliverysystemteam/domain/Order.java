package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.OrderApplication;
import hanwhadeliverysystemteam.domain.MenuCancelled;
import hanwhadeliverysystemteam.domain.MenuOrdered;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Order_table")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String menuName;

    private Integer orderId;

    private String address;

    private Integer qty;

    private String orderStatus;

    @PostPersist
    public void onPostPersist() {
        MenuCancelled menuCancelled = new MenuCancelled(this);
        menuCancelled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        hanwhadeliverysystemteam.external.Payment payment = new hanwhadeliverysystemteam.external.Payment();
        // mappings goes here
        OrderApplication.applicationContext
            .getBean(hanwhadeliverysystemteam.external.PaymentService.class)
            .pay(payment);

        MenuOrdered menuOrdered = new MenuOrdered(this);
        menuOrdered.publishAfterCommit();
    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(
            OrderRepository.class
        );
        return orderRepository;
    }
}
