package academy.devdojo.controller;


import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.config.IntegrationTestContainers;
import academy.devdojo.response.ProfileGetResponse;
import academy.devdojo.response.ProfilePostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfileControllerIT extends IntegrationTestContainers {
    private static final String URL = "/v1/profiles";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProfileUtils profileUtils;

    @Test
    @Sql("/sql/profile/init_two_profiles.sql")
    @DisplayName("findAll() must return a list of all Profiles")
    void findAll_ReturnProfiles_WhenSuccessful() {
        var typeReference = new ParameterizedTypeReference<List<ProfileGetResponse>>() {};

        var response = testRestTemplate.exchange(URL + "/list", HttpMethod.GET, null, typeReference);

        assertThat(response).isNotNull();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody()).isNotNull().doesNotContainNull();

        response.getBody().forEach(profileGetResponse -> assertThat(profileGetResponse).hasNoNullFieldsOrProperties());


    }

    @Test
    @DisplayName("findAll() returns an empty list when no profiles are found")
    void findAll_ReturnsEmptyList_WhenNoProfilesAreFound() {
        var typeReference = new ParameterizedTypeReference<List<ProfileGetResponse>>() {};

        var response = testRestTemplate.exchange(URL + "/list", HttpMethod.GET, null, typeReference);

        assertThat(response).isNotNull();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save() Creates profile")
    void save_CreatesProfile_WhenSuccessful() {

        var profileToSave = profileUtils.newProfileToSave();

        var response = testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(profileToSave), ProfilePostResponse.class);

        assertThat(response).isNotNull();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(response.getBody()).isNotNull().hasNoNullFieldsOrProperties();

    }


}
