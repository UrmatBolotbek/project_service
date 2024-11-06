package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InternshipMapper {

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    Internship toInternship(InternshipDto internshipDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    InternshipDto toInternshipDto(Internship internship);


}
