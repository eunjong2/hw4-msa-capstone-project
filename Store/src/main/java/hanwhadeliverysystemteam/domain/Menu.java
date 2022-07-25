package hanwhadeliverysystemteam.domain;

import hanwhadeliverysystemteam.StoreApplication;
import hanwhadeliverysystemteam.domain.CookFinished;
import hanwhadeliverysystemteam.domain.CookStarted;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

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

    private Integer orderId;


    @PostPersist
    public void onPostPersist() {
        CookStarted cookStarted = new CookStarted(this);
        cookStarted.publishAfterCommit();

        CookFinished cookFinished = new CookFinished(this);
        cookFinished.publishAfterCommit();
    }

    public static MenuRepository repository() {
        MenuRepository menuRepository = StoreApplication.applicationContext.getBean(
            MenuRepository.class
        );
        return menuRepository;
    }

    public static void addOrderList(PaymentAgreed paymentAgreed) {
        /** Example 1:  new item 
        Menu menu = new Menu();
        repository().save(menu);

        */

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

        /** Example 2:  finding and process
        
        repository().findById(paymentCancelled.get???()).ifPresent(menu->{
            
            menu // do something
            repository().save(menu);


         });
        */

    }
}
