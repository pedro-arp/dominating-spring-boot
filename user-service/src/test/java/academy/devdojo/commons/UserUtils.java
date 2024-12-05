package academy.devdojo.commons;

import academy.devdojo.domain.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserUtils {
    public List<User> newUserList() {
        var user1 = User.builder().id(1L).firstName("Kiara").lastName("Violante").email("kiara@hotmail.com").roles("USER").password("{bcrypt}$2a$10$BmWgGrkptSleGv6mv.nQ0OJnPMf61vjZUTiX7m6bAh/xYhEKukUP2").build();

        var user2 = User.builder().id(2L).firstName("Jolie").lastName("Augusta").email("jolie@hotmail.com").roles("USER").password("{bcrypt}$2a$10$BmWgGrkptSleGv6mv.nQ0OJnPMf61vjZUTiX7m6bAh/xYhEKukUP2").build();

        var user3 = User.builder().id(3L).firstName("Bruce").lastName("Banner").email("bruce@hotmail.com").roles("USER").password("{bcrypt}$2a$10$BmWgGrkptSleGv6mv.nQ0OJnPMf61vjZUTiX7m6bAh/xYhEKukUP2").build();

        return new ArrayList<>(List.of(user1, user2, user3));
    }

    public User newUserSaved() {
        return User.builder().id(99L).firstName("Test").lastName("Ing").email("testing@hotmail.com").roles("USER").password("{bcrypt}$2a$10$BmWgGrkptSleGv6mv.nQ0OJnPMf61vjZUTiX7m6bAh/xYhEKukUP2").build();
    }
}
