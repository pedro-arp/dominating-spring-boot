package academy.devdojo.service;

import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.commons.UserProfileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.UserProfile;
import academy.devdojo.repository.UserProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(UserUtils.class)
class UserProfileServiceTest {

    private List<UserProfile> userProfiles;

    @Mock
    private UserProfileRepository repository;

    @InjectMocks
    private UserProfileUtils userProfileUtils;

    @InjectMocks
    private UserProfileService service;

    @Spy
    private UserUtils users;
    @Spy
    private ProfileUtils profiles;

    @BeforeEach
    void init() {

        userProfiles = userProfileUtils.newUserProfileList();

    }

    @Test
    @DisplayName("findAll() returns a list with all UserProfiles")
    public void findAll_ReturnsAllProfiles_WhenSuccessful() {

        BDDMockito.when(repository.findAll()).thenReturn(this.userProfiles);

        var test = service.findAll();

        Assertions.assertThat(test).hasSameElementsAs(this.userProfiles);

    }

    @Test
    @DisplayName("findAllUserByProfileId() returns a list with all users by profile")
    public void findAllUserByProfileId_ReturnsAllUsersByProfile_WhenSuccessful() {

        var id = 99L;

        var usersByProfile = this.userProfiles
                .stream()
                .filter(userProfile -> userProfile.getProfile().getId().equals(id))
                .map(UserProfile::getUser)
                .toList();

        BDDMockito.when(repository.findAllUserByProfileId(id)).thenReturn(usersByProfile);

        var users = service.findAllUserByProfileId(id);

        Assertions.assertThat(users).doesNotContainNull().hasSize(1).hasSameElementsAs(usersByProfile);

        users.forEach(user -> Assertions.assertThat(user).hasNoNullFieldsOrProperties());

    }

}