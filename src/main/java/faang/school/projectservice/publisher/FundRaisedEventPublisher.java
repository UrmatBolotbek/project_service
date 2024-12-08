package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import faang.school.projectservice.dto.event.FundRaisedEvent;

@Slf4j
@Component
public class FundRaisedEventPublisher extends EventPublisherAbstract<FundRaisedEvent> {

    @Value("${spring.data.redis.channels.fund-raised-channel}")
    private String fundRaisedTopic;

    public FundRaisedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(FundRaisedEvent fundRaisedEvent) {
        handleEvent(fundRaisedEvent, fundRaisedTopic);
    }
}
