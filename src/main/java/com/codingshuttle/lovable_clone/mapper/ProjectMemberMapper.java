package com.codingshuttle.lovable_clone.mapper;

import com.codingshuttle.lovable_clone.dto.member.MemberResponse;
import com.codingshuttle.lovable_clone.entity.ProjectMember;
import com.codingshuttle.lovable_clone.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "projectRole",constant = "OWNER")
    MemberResponse toProjectMemberResponseFromUser(User owner);

    @Mapping(source = "id.userId", target = "id")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "role", target = "projectRole")
    @Mapping(source = "invitedAt", target = "invitedAt")
    MemberResponse toMemberResponseFromProjectMember(ProjectMember projectMember);
}
