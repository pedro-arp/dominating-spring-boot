package academy.devdojo.commons;

import academy.devdojo.domain.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserProfileUtils {

    private final UserUtils users;
    private final ProfileUtils profiles;

    public List<UserProfile> newUserProfileList() {

        var userProfile1 = UserProfile.builder().id(1L).user(users.newUserSaved()).profile(profiles.newProfileSaved()).build();

        return new ArrayList<>(List.of(userProfile1));
    }

}
