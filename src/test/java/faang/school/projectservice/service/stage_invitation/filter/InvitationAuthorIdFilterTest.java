package faang.school.projectservice.service.stage_invitation.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class InvitationAuthorIdFilterTest extends InvitationFilterTest {
    private final InvitationAuthorIdFilter authorIdFilter = new InvitationAuthorIdFilter();

    @Test
    public void testIsApplicable() {
        IsApplicableCheck(authorIdFilter, filter.getAuthorIdPattern(), 1L);
    }

    @Test
    public void testIsNotApplicable() {
        IsNotApplicableCheck(authorIdFilter, filter.getAuthorIdPattern(), 0L);
    }

    @Test
    public void testApplyWithMatchingAuthorId() {
        when(filter.getAuthorIdPattern()).thenReturn(1L);
        when(teamMember.getUserId()).thenReturn(1L);

        StageInvitation invitation = new StageInvitation();
        invitation.setAuthor(teamMember);

        Stream<StageInvitation> invitations = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitations = authorIdFilter.apply(invitations, filter);

        assertEquals(1, sortedInvitations.count());
    }

    @Test
    public void testApplyWithNonMatchingAuthorId() {
        when(filter.getAuthorIdPattern()).thenReturn(2L);
        when(teamMember.getUserId()).thenReturn(1L);

        StageInvitation invitation = new StageInvitation();
        invitation.setAuthor(teamMember);

        Stream<StageInvitation> invitations = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitations = authorIdFilter.apply(invitations, filter);

        assertEquals(0, sortedInvitations.count());
    }
}
