package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentRequestFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MomentMonthFilterTest {

    private MomentMonthFilter momentMonthFilter;
    private MomentRequestFilterDto filterDto;
    private Moment moment;

    @BeforeEach
    void setUp() {
        momentMonthFilter = new MomentMonthFilter();
        filterDto = MomentRequestFilterDto.builder().build();
        moment = new Moment();
        moment.setDate(LocalDateTime.of(2024, Month.NOVEMBER, 18, 12, 0));
    }

    @Test
    void shouldReturnFalseWhenMonthPatternIsNull() {
        filterDto.setMonthPattern(null);

        boolean isApplicable = momentMonthFilter.isApplicable(filterDto);

        assertThat(isApplicable).isFalse();
    }

    @Test
    void shouldReturnTrueWhenMonthPatternIsValid() {
        filterDto.setMonthPattern(Month.NOVEMBER);

        boolean isApplicable = momentMonthFilter.isApplicable(filterDto);

        assertThat(isApplicable).isTrue();
    }

    @Test
    void shouldReturnMomentWhenMonthMatches() {
        filterDto.setMonthPattern(Month.NOVEMBER);

        Stream<Moment> resultStream = momentMonthFilter.apply(Stream.of(moment), filterDto);

        assertThat(resultStream).containsExactly(moment);
    }

    @Test
    void shouldReturnEmptyStreamWhenMonthDoesNotMatch() {
        filterDto.setMonthPattern(Month.FEBRUARY);

        Stream<Moment> resultStream = momentMonthFilter.apply(Stream.of(moment), filterDto);

        assertThat(resultStream).isEmpty();
    }
}
