package hanwhadeliverysystemteam.infra;

import hanwhadeliverysystemteam.domain.*;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
    collectionResourceRel = "statusChecks",
    path = "statusChecks"
)
public interface StatusCheckRepository
    extends PagingAndSortingRepository<StatusCheck, Long> {
    List<StatusCheck> findByOrderId(String orderId);
    // keep

}
