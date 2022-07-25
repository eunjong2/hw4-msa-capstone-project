package hanwhadeliverysystemteam.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "StatusCheck_table")
@Data
public class StatusCheck {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String status;
    private String orderId;
}
