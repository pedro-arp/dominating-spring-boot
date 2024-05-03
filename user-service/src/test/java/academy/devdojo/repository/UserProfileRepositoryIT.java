package academy.devdojo.repository;

import academy.devdojo.config.IntegrationTestContainers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserProfileRepositoryIT extends IntegrationTestContainers {

    @Autowired
    private UserProfileRepository repository;

    @Test
    @DisplayName("findAllUserByProfileId() must return a list of all users found by profile id")
    @Order(2)
    @Sql("/sql/user-profile/init_user_profile_2_users_1_profile.sql")
    public void findAllUserByProfileId_ReturnUsers_WhenSuccessful() {

        var size = 2;

        var profileId = 1L;

        var users = repository.findAllUserByProfileId(profileId);

        Assertions.assertThat(users).isNotEmpty().hasSize(size);

        users.forEach(user -> Assertions.assertThat(user).hasNoNullFieldsOrProperties());
    }


}