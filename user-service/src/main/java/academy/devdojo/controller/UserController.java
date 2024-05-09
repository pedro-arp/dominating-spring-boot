package academy.devdojo.controller;

import academy.devdojo.exception.ApiError;
import academy.devdojo.exception.DefaultErrorMessage;
import academy.devdojo.mapper.UserMapper;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import academy.devdojo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/users"})
@RequiredArgsConstructor
@Log4j2
@Tag(name = "User API", description = "User related endpoints")
@SecurityRequirement(name = "basicAuth")
public class UserController {

    private final UserService service;

    private final UserMapper mapper;


    @GetMapping("list")
    @PreAuthorize("hasRole('ADMIN')")

    @Operation(summary = "Get All Users", description = "Get all users available in the system",
            responses = {
                    @ApiResponse(description = "List all users",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserGetResponse.class))))
            })
    public ResponseEntity<List<UserGetResponse>> list() {

        log.debug("Request received to list all users");

        var users = service.findAll();

        var response = mapper.usersToGetResponseList(users);

        return ResponseEntity.ok(response);

    }

    @GetMapping("{id}")
    @Operation(summary = "Get User by Id",
            responses = {
                    @ApiResponse(description = "Find user by id",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserGetResponse.class))),

                    @ApiResponse(description = "User Not Found",
                            responseCode = "404",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorMessage.class)))
            })
    public ResponseEntity<UserGetResponse> findById(@PathVariable @Parameter(description = "user id") Long id) {

        log.info("Request received find user by id '{}'", id);

        var user = service.findById(id);

        var response = mapper.toUserGetResponse(user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("filter")
    public ResponseEntity<UserGetResponse> findByFirstName(@RequestParam(required = false) String firstName) {
        log.info("Request received to list all users, param name '{}'", firstName);

        var userFound = service.findByFirstName(firstName);

        var userGetResponses = mapper.toUserGetResponse(userFound);

        return ResponseEntity.ok(userGetResponses);
    }


    @PostMapping
    @Operation(summary = "Create User",
            responses = {
                    @ApiResponse(description = "Save a user in database",
                            responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserPostResponse.class)
                            )),
                    @ApiResponse(description = "Email already exists",
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            ))
            })
    public ResponseEntity<UserPostResponse> save(@RequestBody @Valid UserPostRequest request) {

        log.info("Request received save a user '{}'", request);

        var user = mapper.toUser(request);

        var userToSave = service.save(user);

        var response = mapper.toUserPostResponse(userToSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        log.info("Request received to delete the user by id '{}'", id);

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UserPutRequest request) {

        log.info("Request received to update the user '{}'", request);

        var userToUpdate = mapper.toUser(request);

        service.update(userToUpdate);

        return ResponseEntity.noContent().build();
    }

}
