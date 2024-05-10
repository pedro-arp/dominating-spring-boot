package academy.devdojo.mapper;

import academy.devdojo.annotation.EncodedMapping;
import academy.devdojo.domain.User;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE
, uses = PasswordEncoderMapper.class)
public interface UserMapper {

    @Mapping(target = "roles", constant = "USER")

    @Mapping(target = "password", qualifiedBy = EncodedMapping.class)

    User toUser(UserPostRequest request);

    UserPostResponse toUserPostResponse(User user);

    User toUser(UserPutRequest request);

    UserGetResponse toUserGetResponse(User user);

    List<UserGetResponse> usersToGetResponseList(List<User> users);
}
