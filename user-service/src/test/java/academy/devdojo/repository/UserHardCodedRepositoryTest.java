package academy.devdojo.repository;


import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
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
class UserHardCodedRepositoryTest {

    private List<User> users;
    @Mock
    private UserData userData;
    @InjectMocks
    private UserHardCodedRepository repository;
    @InjectMocks
    private UserUtils userUtils;

    @BeforeEach
    void init() {

        users = userUtils.newUserList();

        BDDMockito.when(userData.getUsers()).thenReturn(users);
    }

    @Test
    @DisplayName("findAll() must return a list of all users")
    public void findAll_ReturnUsers_WhenSuccessful() {

        var user = repository.findAll();

        Assertions.assertThat(user).hasSameElementsAs(this.users);
    }

    @Test
    @DisplayName("findById()")
    public void findById_ReturnUserFound_WhenSuccessful() {

        var user = repository.findById(1L);

        Assertions.assertThat(user).contains(this.users.get(0));
    }

    @Test
    @DisplayName("save() Create a User")
    public void save_CreateUser_WhenSuccessful() {
        var userToSave = userUtils.newUserSaved();

        var user = repository.save(userToSave);

        Assertions.assertThat(user).isEqualTo(userToSave).hasNoNullFieldsOrProperties();

        repository.findAll();

        Assertions.assertThat(users).contains(userToSave);

    }

    @Test
    @DisplayName("delete() Remove a User")
    public void delete_RemoveUser_WhenSuccessful() {

        var userToDelete = this.users.get(0);

        repository.delete(userToDelete);

        Assertions.assertThat(this.users).doesNotContain(userToDelete);
    }

    @Test
    @DisplayName("update() Update a User")
    public void update_UpdateUser_WhenSuccessful() {

        var userToUpdate = this.users.get(0);

        userToUpdate.setFirstName("Updating");
        userToUpdate.setLastName("ing");
        userToUpdate.setEmail("updating@hotmail.com");

        repository.update(userToUpdate);

        Assertions.assertThat(this.users).contains(userToUpdate);

        this.users.stream().filter(users -> users.getId().equals(userToUpdate.getId())).findFirst()
                .ifPresent(user -> Assertions.assertThat(user.getFirstName()).isEqualTo(userToUpdate.getFirstName()));

    }


}