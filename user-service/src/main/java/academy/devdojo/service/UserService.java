package academy.devdojo.service;

import academy.devdojo.domain.User;
import academy.devdojo.exception.InvalidEmailException;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User findByFirstName(String firstName) {
        return repository.findByFirstName(firstName).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional
    public User save(User user) {
        assertEmailIsUnique(user.getEmail(), user.getId());
        return repository.save(user);

    }

    public void delete(Long id) {

        var userToDelete = findById(id);

        repository.delete(userToDelete);
    }

    public void update(User partialUserToUpdate) {

        var savedUser = findById(partialUserToUpdate.getId());

        assertEmailIsUnique(partialUserToUpdate.getEmail(), partialUserToUpdate.getId());

        var password = partialUserToUpdate.getPassword() == null ? savedUser.getPassword() : partialUserToUpdate.getPassword();

        var roles = savedUser.getRoles();

        var userToUpdate = partialUserToUpdate.withRoles(roles).withPassword(password);

        repository.save(userToUpdate);
    }

    private void assertEmailIsUnique(String email, Long userId) {
        repository.findByEmail(email)
                .ifPresent(userFound -> {
                    if (!userFound.getId().equals(userId)) {
                        throw new InvalidEmailException("email '%s' is already in use".formatted(email));
                    }
                });
    }

}
