package academy.devdojo.commons;

import academy.devdojo.domain.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileUtils {
    public List<Profile> newProfileList() {

        var profile1 = Profile.builder().id(1L).name("Administrator").description("Able to admin everything").build();

        var profile2 = Profile.builder().id(2L).name("Manager").description("Can manage some stuff").build();

        return new ArrayList<>(List.of(profile1, profile2));
    }

    public Profile newProfileToSave() {
        return Profile.builder().name("Test").description("Testing create profile").build();
    }

    public Profile newProfileSaved() {
        return Profile.builder().id(99L).name("Test").description("Testing create profile").build();
    }
}
