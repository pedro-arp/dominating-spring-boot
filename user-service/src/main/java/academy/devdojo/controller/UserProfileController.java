package academy.devdojo.controller;

import academy.devdojo.mapper.UserProfileMapper;
import academy.devdojo.response.UserProfileGetResponse;
import academy.devdojo.response.UserProfileUserGetResponse;
import academy.devdojo.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/user-profiles", "v1/user-profiles/"})
@RequiredArgsConstructor
@Log4j2
public class UserProfileController {

    private final UserProfileService service;

    private final UserProfileMapper mapper;

    @GetMapping("list")
    public ResponseEntity<List<UserProfileGetResponse>> list() {

        log.debug("Request received to list all users usersProfile");

        var userProfiles = service.findAll();

        var response = mapper.toUserProfileGetResponse(userProfiles);

        return ResponseEntity.ok(response);

    }

    @GetMapping("profiles/{id}/users")
    public ResponseEntity<List<UserProfileUserGetResponse>> findByProfileId(@PathVariable Long id) {

        log.debug("Request received to list all Users by Profile ID '{}'", id);

        var allUserByProfileId = service.findAllUserByProfileId(id);

        var response = mapper.userByProfileIdResponseList(allUserByProfileId);

        return ResponseEntity.ok(response);

    }
}
