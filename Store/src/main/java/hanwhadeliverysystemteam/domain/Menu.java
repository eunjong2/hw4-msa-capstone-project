package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.StoreApplication;
import hanwhadeliverysystemteam.domain.CookFinished;
import hanwhadeliverysystemteam.domain.CookStarted;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "Menu_table")
@Data
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String menuName;

    private Integer qty;

    private String cookStatus;

    private Long orderId;
    private String address;
    private Double price;


    @PostPersist
    public void onPostPersist() {
        CookStarted cookStarted = new CookStarted(this);
        
        cookStarted.publishAfterCommit();

        // CookFinished cookFinished = new CookFinished(this);
        // cookFinished.publishAfterCommit();
    }

    public static MenuRepository repository() {
        MenuRepository menuRepository = StoreApplication.applicationContext.getBean(
            MenuRepository.class
        );
        return menuRepository;
    }
    public void accept() {
        System.out.println("호출됨");
        CookStarted cookStarted = new CookStarted(this);
        
        cookStarted.setCookStatus("Accepted");
        
        
        cookStarted.publishAfterCommit();
    }

    
    public void finish() {
        CookFinished cookFinished = new CookFinished(this);
        cookFinished.setCookStatus("Finished");
        cookFinished.publishAfterCommit();
    }
    public static void addOrderList(PaymentAgreed paymentAgreed) {
      

        Menu menu = new Menu();
        menu.setCookStatus(paymentAgreed.getPaymentStatus());
        menu.setMenuName(paymentAgreed.getMenuName());
        menu.setOrderId(paymentAgreed.getOrderId());
        menu.setQty(paymentAgreed.getQty());
        menu.setAddress(paymentAgreed.getAddress());
        menu.setPrice(paymentAgreed.getPrice());

        repository().save(menu);

        

        /** Example 2:  finding and process
        
        repository().findById(paymentAgreed.get???()).ifPresent(menu->{
            
            menu // do something
            repository().save(menu);


         });
        */

    }

    public static void cancel(PaymentCancelled paymentCancelled) {
        /** Example 1:  new item 
        Menu menu = new Menu();
        repository().save(menu);

        */

        // Example 2:  finding and process
 
        repository().findById(paymentCancelled.getOrderId()).ifPresent(menu->{
            
            //menu // do something
            if("Ordered".equals(menu.getCookStatus()))
            {

             repository().delete(menu);
            }
            //repository().save(menu);


         });
        

    }
}
