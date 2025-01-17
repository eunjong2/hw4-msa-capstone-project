package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.PaymentApplication;
import hanwhadeliverysystemteam.domain.PaymentAgreed;
import hanwhadeliverysystemteam.domain.PaymentCancelled;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Payment_table")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long orderId;

    private String paymentStatus;
    private String address;
    private Double price;

    private String menuName;

    private Integer qty;

    

    

    @PostPersist
    public void onPostPersist() {
        PaymentAgreed paymentAgreed = new PaymentAgreed(this);
        paymentAgreed.publishAfterCommit();

        // PaymentCancelled paymentCancelled = new PaymentCancelled(this);
        // paymentCancelled.publishAfterCommit();
    }

    public static PaymentRepository repository() {
        PaymentRepository paymentRepository = PaymentApplication.applicationContext.getBean(
            PaymentRepository.class
        );
        return paymentRepository;
    }

    public static void cancelPayment(MenuCancelled menuCancelled) {
        /** Example 1:  new item 
        Payment payment = new Payment();
        repository().save(payment);

        PaymentCancelled paymentCancelled = new PaymentCancelled(payment);
        paymentCancelled.publishAfterCommit();
        */

        // Example 2:  finding and process
        
        repository().findById(menuCancelled.getOrderId()).ifPresent(payment->{
            
            //repository().save(payment);
            
            repository().delete(payment);
            PaymentCancelled paymentCancelled = new PaymentCancelled(payment);
            paymentCancelled.publishAfterCommit();

         });
        

    }
}
