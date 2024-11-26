package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "interns", ignore = true)
    Internship toInternship(InternshipDto internshipDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "interns", target = "internsId", qualifiedByName = "toIds")
    InternshipDto toInternshipDto(Internship internship);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    Internship toInternship(InternshipUpdateDto internshipUpdateDto);

    List<Internship> toInternships(List<InternshipDto> internshipDtos);

    List<InternshipDto> toInternshipDtos(List<Internship> internships);

    @Named("toIds")
    default List<Long> toInternshipIds(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).toList();
    }

}
