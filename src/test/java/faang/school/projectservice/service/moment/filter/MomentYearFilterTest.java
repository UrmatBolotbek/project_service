package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentRequestFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MomentYearFilterTest {
    private MomentYearFilter momentYearFilter;
    private MomentRequestFilterDto filterDto;
    private Moment moment;

    @BeforeEach
    void setUp() {
        momentYearFilter = new MomentYearFilter();
        filterDto = MomentRequestFilterDto.builder().build();
        moment = new Moment();
        moment.setDate(LocalDateTime.of(2024, 1, 1, 12, 0));
    }

    @Test
    void shouldReturnTrueWhenYearPatternIsSet() {
        filterDto.setYearPattern(2024);

        boolean isApplicable = momentYearFilter.isApplicable(filterDto);

        assertThat(isApplicable).isTrue();
    }

    @Test
    void shouldReturnFalseWhenYearPatternIsNotSet() {
        boolean isApplicable = momentYearFilter.isApplicable(filterDto);

        assertThat(isApplicable).isFalse();
    }

    @Test
    void shouldReturnMomentWhenYearMatches() {
        filterDto.setYearPattern(2024);

        Stream<Moment> resultStream = momentYearFilter.apply(Stream.of(moment), filterDto);

        assertThat(resultStream).containsExactly(moment);
    }

    @Test
    void shouldReturnEmptyStreamWhenYearDoesNotMatch() {
        filterDto.setYearPattern(2023);

        Stream<Moment> resultStream = momentYearFilter.apply(Stream.of(moment), filterDto);

        assertThat(resultStream).isEmpty();
    }
}
