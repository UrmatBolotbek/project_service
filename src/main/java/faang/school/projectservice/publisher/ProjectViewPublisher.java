package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectViewPublisher extends EventPublisherAbstract<ProjectViewEvent> {

    @Value("${spring.data.redis.channels.project-view-channel}")
    private String topicGoalCompleted;

    public ProjectViewPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }


    public void publish(ProjectViewEvent event) {
        handleEvent(event, topicGoalCompleted);
    }
}
