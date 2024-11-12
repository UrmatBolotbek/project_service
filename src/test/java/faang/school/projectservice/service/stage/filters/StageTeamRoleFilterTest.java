package faang.school.projectservice.service.stage.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class StageTeamRoleFilterTest {

    private StageTeamRoleFilter stageTeamRoleFilter;

    private Stage stage1;
    private Stage stage2;
    private StageRoles stageRoles1;
    private StageRoles stageRoles2;
    private StageFilterDto filters;

    @BeforeEach
    void setUp() {
        stage1 = new Stage();
        stage2 = new Stage();
        stageRoles1 = new StageRoles();
        stageRoles2 = new StageRoles();
        stageTeamRoleFilter = new StageTeamRoleFilter();
        filters = new StageFilterDto();
    }

    @Test
    void testIsApplicableWithNullTeamRole() {
        filters.setTeamRole(null);
        boolean result = stageTeamRoleFilter.isApplicable(filters);
        assertFalse(result);
    }

    @Test
    void testIsApplicableWithNonNullTeamRole() {
        filters.setTeamRole(TeamRole.OWNER);
        boolean result = stageTeamRoleFilter.isApplicable(filters);
        assertTrue(result);
    }

    @Test
    void testApplyWithMatchingTeamRole() {
        filters.setTeamRole(TeamRole.OWNER);

        stageRoles1.setTeamRole(TeamRole.OWNER);
        stageRoles2.setTeamRole(TeamRole.DESIGNER);

        stage1.setStageRoles(List.of(stageRoles1));
        stage2.setStageRoles(List.of(stageRoles2));

        List<Stage> stages = List.of(stage1, stage2);

        List<Stage> filteredStages = stageTeamRoleFilter.apply(stages, filters);

        assertEquals(1, filteredStages.size());
        assertTrue(filteredStages.contains(stage1));
        assertFalse(filteredStages.contains(stage2));
    }

    @Test
    void testApplyWithNonMatchingTeamRole() {
        filters.setTeamRole(TeamRole.INTERN);

        stageRoles1.setTeamRole(TeamRole.OWNER);
        stageRoles2.setTeamRole(TeamRole.DESIGNER);

        stage1.setStageRoles(List.of(stageRoles1));
        stage2.setStageRoles(List.of(stageRoles2));

        List<Stage> stages = List.of(stage1, stage2);

        List<Stage> filteredStages = stageTeamRoleFilter.apply(stages, filters);

        assertTrue(filteredStages.isEmpty());
    }
}