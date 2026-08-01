package com.codingshuttle.lovable_clone.mapper;

import com.codingshuttle.lovable_clone.dto.auth.SignupRequest;
import com.codingshuttle.lovable_clone.dto.auth.UserProfileResponse;
import com.codingshuttle.lovable_clone.entity.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest request);

    UserProfileResponse toUserProfileResponse(User user);

}
