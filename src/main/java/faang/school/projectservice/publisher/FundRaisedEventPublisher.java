package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import faang.school.projectservice.event.FundRaisedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FundRaisedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.fund-raised-channel.name}")
    private String fundRaisedTopic;

    public void publish(FundRaisedEvent fundRaisedEvent) {
        try {
            String json = objectMapper.writeValueAsString(fundRaisedEvent);
            redisTemplate.convertAndSend(fundRaisedTopic, json);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while working with JSON: ", e);
            throw new RuntimeException(e);
        }
    }
}
