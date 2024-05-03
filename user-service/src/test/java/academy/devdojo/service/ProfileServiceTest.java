package academy.devdojo.service;

import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.domain.Profile;
import academy.devdojo.repository.ProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileServiceTest {

    private List<Profile> profiles;

    @Mock
    private ProfileRepository repository;

    @InjectMocks
    private ProfileUtils profileUtils;

    @InjectMocks
    private ProfileService service;

    @BeforeEach
    void init() {
        profiles = profileUtils.newProfileList();
    }

    @Test
    @DisplayName("findAll() returns a list with all Profiles")
    public void findAll_ReturnsAllProfiles_WhenSuccessful() {

        BDDMockito.when(repository.findAll()).thenReturn(this.profiles);

        var test = service.findAll();

        Assertions.assertThat(test).hasSameElementsAs(profiles);

    }

    @Test
    @DisplayName("save() Create Profile")
    public void save_CreateProfile_WhenSuccessful() {

        var profileToSave = profileUtils.newProfileToSave();

        var profileSaved = profileUtils.newProfileSaved();

        BDDMockito.when(repository.save(profileToSave)).thenReturn(profileSaved);

        var profile = service.save(profileToSave);

        Assertions.assertThat(profile).isEqualTo(profileSaved).hasNoNullFieldsOrProperties();


    }
}