package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VacancyPositionFilterTest {
    private VacancyPositionFilter vacancyPositionFilter;
    private VacancyFilterDto vacancyFilterDto;
    private Stream<Vacancy> vacancyStream;

    @BeforeEach
    void setUp() {
        vacancyPositionFilter = new VacancyPositionFilter();
        vacancyFilterDto = new VacancyFilterDto();

        vacancyStream = Stream.of(
                Vacancy.builder().description("Backend Developer Role").build(),
                Vacancy.builder().description("Frontend Developer Role").build(),
                Vacancy.builder().description("Fullstack Developer Role").build()
        );
    }

    @Test
    void testIsApplicableTrue() {
        vacancyFilterDto.setPositionPattern("Developer Role");
        assertTrue(vacancyPositionFilter.isApplicable(vacancyFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(vacancyPositionFilter.isApplicable(vacancyFilterDto));
    }

    @Test
    void testApply() {
        vacancyFilterDto.setPositionPattern("Backend");
        List<Vacancy> resultList = vacancyPositionFilter
                .apply(vacancyStream, vacancyFilterDto)
                .toList();

        assertEquals(1, resultList.size());
        resultList.forEach(vacancy ->
                assertTrue(vacancy.getDescription().contains(vacancyFilterDto.getPositionPattern())));
    }
}
