package academy.devdojo.repository;

import academy.devdojo.domain.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class UserData {
    private final List<User> users = new ArrayList<>();

    {
        var user1 = User.builder().id(1L).firstName("Pedro").lastName("Pereira").email("pedro@hotmail.com").build();
        var user2 = User.builder().id(2L).firstName("Bianca").lastName("Violante").email("bianca@hotmail.com").build();
        var user3 = User.builder().id(3L).firstName("Peter").lastName("Augusto").email("peter@hotmail.com").build();

        users.addAll(List.of(user1, user2, user3));

    }

}
