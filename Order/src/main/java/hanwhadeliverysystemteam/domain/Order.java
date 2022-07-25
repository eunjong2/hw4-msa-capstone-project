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

    private Long orderId;

    private String address;

    private Integer qty;

    private String orderStatus;
    
    private Double price;

    

    @PostPersist
    public void onPostPersist() {
        // MenuCancelled menuCancelled = new MenuCancelled(this);
        // menuCancelled.publishAfterCommit();


        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
        MenuOrdered menuOrdered = new MenuOrdered(this);
        menuOrdered.publishAfterCommit();
        
        hanwhadeliverysystemteam.external.Payment payment = new hanwhadeliverysystemteam.external.Payment();
        payment.setOrderId(menuOrdered.getOrderId());
        payment.setPaymentStatus("Ordered");
        payment.setPrice(menuOrdered.getPrice());
        payment.setQty(menuOrdered.getQty());
        payment.setMenuName(menuOrdered.getMenuName());
        payment.setAddress(menuOrdered.getAddress());

        // mappings goes here
        OrderApplication.applicationContext
            .getBean(hanwhadeliverysystemteam.external.PaymentService.class)
            .pay(payment);

        // MenuOrdered menuOrdered = new MenuOrdered(this);
        // menuOrdered.publishAfterCommit();
    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(
            OrderRepository.class
        );
        return orderRepository;
    }
    public void cancel() {
        MenuCancelled menuCancelled = new MenuCancelled(this);
        // menuCancelled
        
        
        menuCancelled.publishAfterCommit();
    }
}
