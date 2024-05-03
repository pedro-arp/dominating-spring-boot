package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.mapper.ProfileMapperImpl;
import academy.devdojo.service.ProfileService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(ProfileController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({ProfileMapperImpl.class, ProfileUtils.class, FileUtils.class})
class ProfileControllerTest {
    private static final String URL = "/v1/profiles";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService service;

    @Autowired
    private ProfileUtils profileUtils;

    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("findAll() must return a list of all Profiles")
    @Order(1)
    public void findAll_ReturnProfiles_WhenSuccessful() throws Exception {

        var response = fileUtils.readResourceFile("profile/get-all-profiles-200.json");

        BDDMockito.when(service.findAll()).thenReturn(profileUtils.newProfileList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/list"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() must return a empty list when no profiles are found")
    @Order(2)
    public void findAll_ReturnEmptyList_WhenNoProfilesAreFound() throws Exception {

        var response = fileUtils.readResourceFile("profile/get-all-profiles-empty-list-200.json");

        BDDMockito.when(service.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/list"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("save() Create Profile")
    @Order(5)
    public void save_CreateProfile_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("profile/post-request-profile-201.json");

        var response = fileUtils.readResourceFile("profile/post-response-profile-201.json");

        var profileSaved = profileUtils.newProfileSaved();

        BDDMockito.when(service.save(ArgumentMatchers.any())).thenReturn(profileSaved);

        mockMvc.perform(MockMvcRequestBuilders.post(URL).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @ParameterizedTest
    @MethodSource("postProfileBadRequestSourceFiles")
    @DisplayName("save() returns  bad request when fields are invalid")
    @Order(10)
    public void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {

        var request = fileUtils.readResourceFile("profile/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    private static Stream<Arguments> postProfileBadRequestSourceFiles() {

        var nameError = "The field 'name' is required";
        var descriptionError = "The field 'description' is required";

        var allErrors = List.of(nameError, descriptionError);

        return Stream.of(
                Arguments.of("post-request-profile-blank-fields-400.json", allErrors),
                Arguments.of("post-request-profile-empty-fields-400.json", allErrors));
    }
}