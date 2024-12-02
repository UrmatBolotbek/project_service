package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {
    private static final Long VACANCY_ID = 1L;
    private static final String VACANCY_NAME = "Backend Developer";
    private static final String VACANCY_DESCRIPTION = "Looking for an experienced backend developer.";
    private static final Long PROJECT_ID = 2L;

    private MockMvc mockMvc;

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    private VacancyResponseDto vacancyResponseDto;
    private List<VacancyResponseDto> vacancyResponseList;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vacancyController).build();

        vacancyResponseDto = VacancyResponseDto.builder()
                .id(VACANCY_ID)
                .name(VACANCY_NAME)
                .description(VACANCY_DESCRIPTION)
                .projectId(PROJECT_ID)
                .build();

        vacancyResponseList = Collections.singletonList(vacancyResponseDto);
    }

    @Test
    public void testCreateVacancy() throws Exception {
        when(vacancyService.create(any(VacancyRequestDto.class))).thenReturn(vacancyResponseDto);

        mockMvc.perform(post("/api/v1/vacancies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Backend Developer",
                                    "description": "Looking for an experienced backend developer.",
                                    "projectId": 2,
                                    "createdBy": 1,
                                    "status": "OPEN",
                                    "requiredSkillIds": [1, 2, 3]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(VACANCY_ID))
                .andExpect(jsonPath("$.name").value(VACANCY_NAME))
                .andExpect(jsonPath("$.description").value(VACANCY_DESCRIPTION));

        verify(vacancyService).create(any(VacancyRequestDto.class));
    }

    @Test
    public void testUpdateVacancy() throws Exception {
        when(vacancyService.update(eq(VACANCY_ID), any(VacancyUpdateDto.class))).thenReturn(vacancyResponseDto);

        mockMvc.perform(put("/api/v1/vacancies/" + VACANCY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated Vacancy Name",
                                    "description": "Updated description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VACANCY_ID))
                .andExpect(jsonPath("$.name").value(VACANCY_NAME))
                .andExpect(jsonPath("$.description").value(VACANCY_DESCRIPTION));

        verify(vacancyService).update(eq(VACANCY_ID), any(VacancyUpdateDto.class));
    }

    @Test
    public void testDeleteVacancy() throws Exception {
        mockMvc.perform(delete("/api/v1/vacancies/" + VACANCY_ID))
                .andExpect(status().isNoContent());

        verify(vacancyService).delete(VACANCY_ID);
    }

    @Test
    public void testGetVacancies() throws Exception {
        when(vacancyService.getVacancies(any())).thenReturn(vacancyResponseList);

        mockMvc.perform(get("/api/v1/vacancies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(VACANCY_ID))
                .andExpect(jsonPath("$[0].name").value(VACANCY_NAME))
                .andExpect(jsonPath("$[0].description").value(VACANCY_DESCRIPTION));

        verify(vacancyService).getVacancies(any(VacancyFilterDto.class));
    }

    @Test
    public void testGetVacancy() throws Exception {
        when(vacancyService.getVacancy(VACANCY_ID)).thenReturn(vacancyResponseDto);

        mockMvc.perform(get("/api/v1/vacancies/" + VACANCY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VACANCY_ID))
                .andExpect(jsonPath("$.name").value(VACANCY_NAME))
                .andExpect(jsonPath("$.description").value(VACANCY_DESCRIPTION));

        verify(vacancyService).getVacancy(VACANCY_ID);
    }
}
