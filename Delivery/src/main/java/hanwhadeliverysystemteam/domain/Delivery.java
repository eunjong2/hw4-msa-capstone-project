package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.DeliveryApplication;
import hanwhadeliverysystemteam.domain.DeliveryCompleted;
import hanwhadeliverysystemteam.domain.DeliveryStarted;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Delivery_table")
@Data
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String orderStatus;
    private Long orderId;
    private String menuName;
    private Integer qty;
    private Double price;
    private String address;
    private String deliveryStatus;


    @PostPersist
    public void onPostPersist() {}

    @PrePersist
    public void onPrePersist() {
        DeliveryStarted deliveryStarted = new DeliveryStarted(this);
        deliveryStarted.publishAfterCommit();

        // DeliveryCompleted deliveryCompleted = new DeliveryCompleted(this);
        // deliveryCompleted.publishAfterCommit();
    }

    public static DeliveryRepository repository() {
        DeliveryRepository deliveryRepository = DeliveryApplication.applicationContext.getBean(
            DeliveryRepository.class
        );
        return deliveryRepository;
    }

    public static void addDeliveryList(CookFinished cookFinished) {
        // Example 1:  new item 
        Delivery delivery = new Delivery();


        delivery.setOrderStatus(cookFinished.getCookStatus());
        delivery.setOrderId(cookFinished.getOrderId());
        delivery.setPrice(cookFinished.getPrice());
        delivery.setMenuName(cookFinished.getMenuName());
        delivery.setAddress(cookFinished.getAddress());
        delivery.setQty(cookFinished.getQty());
        
        
        

        repository().save(delivery);

        

        /** Example 2:  finding and process
        
        repository().findById(cookFinished.get???()).ifPresent(delivery->{
            
            delivery // do something
            repository().save(delivery);


         });
        */

    }
}
