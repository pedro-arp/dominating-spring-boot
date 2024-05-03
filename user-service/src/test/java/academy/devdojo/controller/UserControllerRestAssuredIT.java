package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.config.IntegrationTestContainers;
import academy.devdojo.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerRestAssuredIT extends IntegrationTestContainers {
    private static final String URL = "/v1/users";

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private UserRepository repository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("findAll() must return a list of all users")
    @Sql("/sql/user/init_three_users.sql")
    @Order(1)
    public void findAll_ReturnUsers_WhenSuccessful() throws Exception {

        var expectedResponse = fileUtils.readResourceFile("user/get/get-all-users-200.json");

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when()
                .get(URL + "/list")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .and(
                        users -> {
                            users.node("[0].id").isNotNull();
                            users.node("[1].id").isNotNull();
                            users.node("[2].id").isNotNull();
                        }
                );

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("findAll() returns empty list when no users are found")
    @Order(2)

    public void findAll_ReturnsEmptyList_WhenNoUsersFound() throws Exception {

        var response = fileUtils.readResourceFile("user/get/get-all-users-is-empty-list-200.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when()
                .get(URL + "/list")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("findById() return user found by id")
    @Sql("/sql/user/init_one_user.sql")
    @Order(3)

    public void findById_ReturnUserById_WhenSuccessful() throws Exception {

        var expectedResponse = fileUtils.readResourceFile("user/get/get-user-by-id-200.json");

        var users = repository.findAll();

        Assertions.assertThat(users).hasSize(1);

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when()
                .get(URL + "/" + users.get(0).getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .node("id")
                .asNumber()
                .isPositive();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedResponse);

    }


    @Test
    @DisplayName("findById() throw NotFoundException when no user is found")
    @Sql("/sql/user/init_three_users.sql")
    @Order(4)
    public void findById_ThrowsNotFoundException_WhenUserNotFound() throws Exception {

        var idNotFound = 99L;

        var expectedResponse = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");

        var users = repository.findAll();

        Assertions.assertThat(users).doesNotContainNull();

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when()
                .get(URL + "/{id}", idNotFound)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();


    }

    @Test
    @DisplayName("save() Create User")
    @Order(5)
    public void save_CreateUser_WhenSuccessful() throws Exception {

        var request = fileUtils.readResourceFile("user/post/post-request-user-201.json");

        var expectedResponse = fileUtils.readResourceFile("user/post/post-response-user-201.json");

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract().response().body().asString();


        JsonAssertions.assertThatJson(response)
                .node("id")
                .asNumber()
                .isPositive();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("delete() Remove User")
    @Sql("/sql/user/init_one_user.sql")
    @Order(6)
    public void delete_RemoveUser_WhenSuccessful() {


        var users = repository.findAll();

        Assertions.assertThat(users).hasSize(1);

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when()
                .delete(URL + "/" + users.get(0).getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();


    }

    @Test
    @DisplayName("delete() throw NotFoundException no anime is found")
    @Sql("/sql/user/init_three_users.sql")
    @Order(7)
    public void delete_ThrowNotFoundException_WhenIsNotFound() throws Exception {

        var expectedResponse = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");

        var id = 99L;

        var users = repository.findAll();

        Assertions.assertThat(users).doesNotContainNull();

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL + "/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();

    }

    @Test
    @DisplayName("update() Update User")
    @Sql("/sql/user/init_one_user.sql")
    @Order(8)
    public void update_UpdateUser_WhenSuccessful() throws Exception {

        var request = fileUtils.readResourceFile("user/put/put-request-user-200.json");

        var users = repository.findAll();

        Assertions.assertThat(users).hasSize(1);

        request = request.replace("1", users.get(0).getId().toString());

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .body(request)
                .when()
                .put(URL)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

    }

    @Test
    @DisplayName("update() Update User throws Exception when User not Found")
    @Order(9)
    public void update_UpdateUser_ThrowsException() throws Exception {

        var request = fileUtils.readResourceFile("user/put/put-request-user-200.json");

        var expectedResponse = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();
    }

    @ParameterizedTest
    @MethodSource("postUserBadRequestSourceFiles")
    @DisplayName("save() returns  bad request when fields are invalid")
    @Order(10)
    public void save_ReturnsBadRequest_WhenFieldsAreInvalid(String requestFileName, String responseFileName) throws Exception {

        var request = fileUtils.readResourceFile("user/post/%s".formatted(requestFileName));

        var expectedResponse = fileUtils.readResourceFile("user/post/%s".formatted(responseFileName));

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .body(request)
                .when()
                .post(URL)
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response().body().asString();


        JsonAssertions.assertThatJson(response)
                .node("timestamp")
                .asString()
                .isNotEmpty();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);

    }

    private static Stream<Arguments> postUserBadRequestSourceFiles() {

        return Stream.of(
                Arguments.of("post-request-user-blank-fields-400.json", "post-response-user-blank-fields-400.json"),
                Arguments.of("post-request-user-empty-fields-400.json", "post-response-user-empty-fields-400.json"),
                Arguments.of("post-request-user-invalid-email-field-400.json", "post-response-user-invalid-email-field-400.json"));
    }

    @ParameterizedTest
    @DisplayName("update() returns bad request when fields are invalid")
    @MethodSource("putUserBadRequestSourceFiles")
    @Order(11)
    public void update_ReturnsBadRequest_WhenFieldsAreInvalid(String requestFileName, String responseFileName) throws Exception {


        var request = fileUtils.readResourceFile("user/put/%s".formatted(requestFileName));

        var expectedResponse = fileUtils.readResourceFile("user/put/%s".formatted(responseFileName));

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .body(request)
                .when()
                .put(URL)
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response().body().asString();


        JsonAssertions.assertThatJson(response)
                .node("timestamp")
                .asString()
                .isNotEmpty();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);


    }

    private static Stream<Arguments> putUserBadRequestSourceFiles() {

        return Stream.of(
                Arguments.of("put-request-user-blank-fields-400.json", "put-response-user-blank-fields-400.json")
                , Arguments.of("put-request-user-empty-fields-400.json", "put-response-user-empty-fields-400.json")
                , Arguments.of("put-request-user-invalid-email-field-400.json", "put-response-user-invalid-email-field-400.json"));
    }


}