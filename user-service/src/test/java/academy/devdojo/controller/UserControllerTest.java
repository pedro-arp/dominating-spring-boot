package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.mapper.UserMapperImpl;
import academy.devdojo.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(UserController.class)
@Import({UserMapperImpl.class, FileUtils.class, UserUtils.class})
class UserControllerTest {
    private static final String URL = "/v1/users";

    private static final String USER_NOT_FOUND = "User not found";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("findAll() must return a list of all users")
    @Order(1)
    public void findAll_ReturnUsers_WhenSuccessful() throws Exception {

        var response = fileUtils.readResourceFile("user/get/get-all-users-200.json");

        BDDMockito.when(service.findAll()).thenReturn(userUtils.newUserList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/list"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() returns empty list when no users are found")
    @Order(2)

    public void findAll_ReturnsEmptyList_WhenNoUsersFound() throws Exception {

        var response = fileUtils.readResourceFile("user/get/get-all-users-is-empty-list-200.json");

        BDDMockito.when(service.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/list")).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findById() return user found by id")
    @Order(3)

    public void findById_ReturnUserById_WhenSuccessful() throws Exception {
        var id = 1L;

        var response = fileUtils.readResourceFile("user/get/get-user-by-id-200.json");

        var userFound = userUtils.newUserList().stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);

        BDDMockito.when(service.findById(id)).thenReturn(userFound);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(response));

    }


    @Test
    @DisplayName("findById() throw NotFoundException when no user is found")
    @Order(4)
    public void findById_ThrowsNotFoundException_WhenUserNotFound() throws Exception {

        var id = 99L;

        var response = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");

        BDDMockito.when(service.findById(ArgumentMatchers.any()))
                .thenThrow(new NotFoundException(USER_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("save() Create User")
    @Order(5)
    public void save_CreateUser_WhenSuccessful() throws Exception {

        var request = fileUtils.readResourceFile("user/post/post-request-user-201.json");

        var response = fileUtils.readResourceFile("user/post/post-response-user-201.json");

        var userToSave = userUtils.newUserSaved();

        BDDMockito.when(service.save(ArgumentMatchers.any())).thenReturn(userToSave);

        mockMvc.perform(MockMvcRequestBuilders.post(URL).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("delete() Remove User")
    @Order(6)
    public void delete_RemoveUser_WhenSuccessful() throws Exception {

        BDDMockito.doNothing().when(service).delete(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1L)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("delete() throw NotFoundException no anime is found")
    @Order(7)
    public void delete_ThrowNotFoundException_WhenIsNotFound() throws Exception {

        var id = 9999L;

        var response = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");


        BDDMockito.doThrow(new NotFoundException(USER_NOT_FOUND)).when(service).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", id)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound()
        ).andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("update() Update User")
    @Order(8)
    public void update_UpdateUser_WhenSuccessful() throws Exception {

        var request = fileUtils.readResourceFile("user/put/put-request-user-200.json");

        mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("update() Update User throws Exception when User not Found")
    @Order(9)
    public void update_UpdateUser_ThrowsException() throws Exception {

        var request = fileUtils.readResourceFile("user/put/put-request-user-404.json");

        var response = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");

        BDDMockito.doThrow(new NotFoundException(USER_NOT_FOUND)).when(service).update(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @ParameterizedTest
    @MethodSource("postUserBadRequestSourceFiles")
    @DisplayName("save() returns  bad request when fields are invalid")
    @Order(10)
    public void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {

        var request = fileUtils.readResourceFile("user/post/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL).content(request).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    private static Stream<Arguments> postUserBadRequestSourceFiles() {

        var firstNameError = "The field 'fistName' is required";
        var lastNameError = "The field 'lastName' is required";
        var emailNameError = "The email format is not valid";

        var allErrors = List.of(firstNameError, lastNameError, emailNameError);
        var emailError = Collections.singletonList(emailNameError);

        return Stream.of(Arguments.of("post-request-user-blank-fields-400.json", allErrors), Arguments.of("post-request-user-empty-fields-400.json", allErrors), Arguments.of("post-request-user-invalid-email-field-400.json", emailError));
    }

    @ParameterizedTest
    @DisplayName("update() returns bad request when fields are invalid")
    @MethodSource("putUserBadRequestSourceFiles")
    @Order(11)
    public void update_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {

        var request = fileUtils.readResourceFile("user/put/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);


    }

    private static Stream<Arguments> putUserBadRequestSourceFiles() {

        var firstNameError = "The field 'fistName' is required";
        var lastNameError = "The field 'lastName' is required";
        var emailNameError = "The email format is not valid";

        var allErrors = List.of(firstNameError, lastNameError, emailNameError);
        var emailError = Collections.singletonList(emailNameError);

        return Stream.of(
                Arguments.of("put-request-user-blank-fields-400.json", allErrors)
                , Arguments.of("put-request-user-empty-fields-400.json", allErrors)
                , Arguments.of("put-request-user-invalid-email-field-400.json", emailError));
    }


}