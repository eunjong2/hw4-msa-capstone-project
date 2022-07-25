package hanwhadeliverysystemteam.infra;

import hanwhadeliverysystemteam.config.kafka.KafkaProcessor;
import hanwhadeliverysystemteam.domain.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MenuViewHandler {

    @Autowired
    private MenuRepository menuRepository;
    // keep

}
