package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentRequestFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MomentProjectFilterTest {
    private MomentProjectFilter momentProjectFilter;
    private MomentRequestFilterDto filterDto;
    private Moment moment;

    @BeforeEach
    void setUp() {
        momentProjectFilter = new MomentProjectFilter();
        filterDto = MomentRequestFilterDto.builder().build();
        moment = new Moment();
        moment.setProjects(List.of(Project.builder().id(2L).build()));
    }

    @Test
    void shouldReturnTrueWhenProjectsIdsPatternIsSet() {
        filterDto.setProjectsIdsPattern(List.of(2L));

        boolean isApplicable = momentProjectFilter.isApplicable(filterDto);

        assertThat(isApplicable).isTrue();
    }

    @Test
    void shouldReturnFalseWhenProjectsIdsPatternIsEmpty() {
        boolean isApplicable = momentProjectFilter.isApplicable(filterDto);

        assertThat(isApplicable).isFalse();
    }

    @Test
    void shouldReturnMomentWhenProjectIdMatches() {
        filterDto.setProjectsIdsPattern(List.of(2L));

        Stream<Moment> resultStream = momentProjectFilter.apply(Stream.of(moment), filterDto);

        assertThat(resultStream).containsExactly(moment);
    }

    @Test
    void shouldReturnEmptyStreamWhenProjectIdDoesNotMatch() {
        filterDto.setProjectsIdsPattern(List.of(3L));

        Stream<Moment> resultStream = momentProjectFilter.apply(Stream.of(moment), filterDto);

        assertThat(resultStream).isEmpty();
    }
}
