package academy.devdojo.mapper;

import academy.devdojo.domain.User;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toUser(UserPostRequest request);

    UserPostResponse toUserPostResponse(User user);

    User toUser(UserPutRequest request);

    UserGetResponse toUserGetResponse(User user);

    List<UserGetResponse> usersToGetResponseList(List<User> users);
}
