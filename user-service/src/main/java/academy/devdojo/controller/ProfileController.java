package academy.devdojo.controller;

import academy.devdojo.mapper.ProfileMapper;
import academy.devdojo.request.ProfilePostRequest;
import academy.devdojo.response.ProfileGetResponse;
import academy.devdojo.response.ProfilePostResponse;
import academy.devdojo.service.ProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/profiles", "v1/profiles/"})
@RequiredArgsConstructor
@Log4j2
@SecurityRequirement(name = "basicAuth")
public class ProfileController {

    private final ProfileService service;

    private final ProfileMapper mapper;

    @GetMapping("list")
    public ResponseEntity<List<ProfileGetResponse>> list() {

        log.info("Request received to list all profiles");

        var profiles = service.findAll();

        var profileGetResponses = mapper.profilesToGetResponseList(profiles);

        return ResponseEntity.ok(profileGetResponses);

    }

    @PostMapping
    public ResponseEntity<ProfilePostResponse> save(@RequestBody @Valid ProfilePostRequest request) {

        log.info("Request received save a profile '{}'", request);

        var profile = mapper.toProfile(request);

        var profileToSave = service.save(profile);

        var profilePostResponse = mapper.toProfilePostResponse(profileToSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(profilePostResponse);
    }
}
