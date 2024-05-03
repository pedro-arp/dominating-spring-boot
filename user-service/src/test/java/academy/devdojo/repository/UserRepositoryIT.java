package academy.devdojo.repository;

import academy.devdojo.commons.UserUtils;
import academy.devdojo.config.IntegrationTestContainers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserUtils.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryIT extends IntegrationTestContainers {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserUtils userUtils;

    @Test
    @DisplayName("save() creates an user")
    @Order(1)
    void save_CreatesNewUser_WhenSuccessful() {
        var userToSave = userUtils.newUserSaved();
        var user = repository.save(userToSave);
        Assertions.assertThat(user).hasNoNullFieldsOrProperties();

    }

    @Test
    @DisplayName("findAll() must return a list of all users")
    @Order(2)
    @Sql("/sql/user/init_one_user.sql")
    void findAll_ReturnUsers_WhenSuccessful() {

        var user = repository.findAll();

        Assertions.assertThat(user).isNotEmpty();
    }


}