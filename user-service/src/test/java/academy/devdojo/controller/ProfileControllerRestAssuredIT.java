package academy.devdojo.controller;


import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.config.IntegrationTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
public class ProfileControllerRestAssuredIT extends IntegrationTestContainers {
    private static final String URL = "/v1/profiles";

    @Autowired
    private ProfileUtils profileUtils;

    @Autowired
    private FileUtils fileUtils;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUrl() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @Sql("/sql/profile/init_two_profiles.sql")
    @DisplayName("findAll() must return a list of all Profiles")
    void findAll_ReturnProfiles_WhenSuccessful() throws Exception {

        var response = fileUtils.readResourceFile("profile/get-all-profiles-200.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/list")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response));

    }

    @Test
    @DisplayName("findAll() must return a empty list when no profiles are found")
    void findAll_ReturnEmptyList_WhenNoProfilesAreFound() throws Exception {

        var response = fileUtils.readResourceFile("profile/get-all-profiles-empty-list-200.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/list")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("save() Create Profile")
    @Order(5)
    public void save_CreateProfile_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("profile/post-request-profile-201.json");

        var expectedResponse = fileUtils.readResourceFile("profile/post-response-profile-201.json");

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

    @ParameterizedTest
    @MethodSource("postProfileBadRequestSourceFiles")
    @DisplayName("save() returns  bad request when fields are invalid")
    @Order(10)
    public void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, String fileResponse) throws Exception {

        var request = fileUtils.readResourceFile("profile/%s".formatted(fileName));

        var expectedResponse = fileUtils.readResourceFile("profile/%s".formatted(fileResponse));

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().asString();

        JsonAssertions.assertThatJson(response)
                .node("timestamp")
                .asString()
                .isNotEmpty();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);
    }

    private static Stream<Arguments> postProfileBadRequestSourceFiles() {

        return Stream.of(
                Arguments.of("post-request-profile-blank-fields-400.json", "post-response-profile-blank-fields-400.json"),
                Arguments.of("post-request-profile-empty-fields-400.json", "post-response-profile-empty-fields-400.json"));
    }
}
