package academy.devdojo.repository;

import academy.devdojo.domain.User;
import academy.devdojo.domain.UserProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @EntityGraph(value = "fullUserProfile")
    List<UserProfile> findAll();

    @Query("select up.user from UserProfile up where up.profile.id = ?1")
    List<User> findAllUserByProfileId(Long profileId);

}
