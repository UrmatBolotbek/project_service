package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/vacancies")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyResponseDto createVacancy(@Valid @RequestBody VacancyRequestDto vacancyRequestDto) {
        return vacancyService.create(vacancyRequestDto);
    }

    @PutMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.OK)
    public VacancyResponseDto updateVacancy(@PathVariable @NotNull(message = "Vacancy ID should not be null") Long vacancyId,
                                            @Valid @RequestBody VacancyUpdateDto vacancyUpdateDto) {
        return vacancyService.update(vacancyId, vacancyUpdateDto);
    }

    @DeleteMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVacancy(@PathVariable @NotNull(message = "Vacancy ID should not be null") Long vacancyId) {
        vacancyService.delete(vacancyId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<VacancyResponseDto> getVacancies(@Valid @ModelAttribute VacancyFilterDto filter) {
        return vacancyService.getVacancies(filter);
    }

    @GetMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.OK)
    public VacancyResponseDto getVacancy(@PathVariable @NotNull(message = "Vacancy ID should not be null") Long vacancyId) {
        return vacancyService.getVacancy(vacancyId);
    }
}
