package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class VacancyNameFilterTest {
    private VacancyNameFilter vacancyNameFilter;
    private VacancyFilterDto vacancyFilterDto;
    private Stream<Vacancy> vacancyStream;

    @BeforeEach
    public void setUp() {
        vacancyNameFilter = new VacancyNameFilter();
        vacancyFilterDto = new VacancyFilterDto();

        vacancyStream = Stream.of(
                Vacancy.builder().name("Backend Developer").build(),
                Vacancy.builder().name("Frontend Developer").build(),
                Vacancy.builder().name("Fullstack Developer").build()
        );
    }

    @Test
    public void testIsApplicableWithEmptyFilter() {
        assertFalse(vacancyNameFilter.isApplicable(vacancyFilterDto));
    }

    @Test
    public void testIsApplicableSuccessfully() {
        vacancyFilterDto.setNamePattern("Developer");
        assertTrue(vacancyNameFilter.isApplicable(vacancyFilterDto));
    }

    @Test
    public void testApplySuccessfully() {
        vacancyFilterDto.setNamePattern("Backend");
        List<Vacancy> resultList = vacancyNameFilter.apply(vacancyStream, vacancyFilterDto)
                .toList();

        assertEquals(1, resultList.size());
        assertTrue(resultList.stream().anyMatch(vacancy -> vacancy.getName().contains("Backend")));
    }
}

