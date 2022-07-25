package hanwhadeliverysystemteam.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Menu_table")
@Data
public class Menu {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
}
