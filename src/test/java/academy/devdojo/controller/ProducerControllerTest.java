package academy.devdojo.controller;


import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProducerUtils;
import academy.devdojo.config.BeanConfig;
import academy.devdojo.mapper.ProducerMapperImpl;
import academy.devdojo.repository.ProducerData;
import academy.devdojo.repository.ProducerHardCodedRepository;
import academy.devdojo.service.ProducerService;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


@WebMvcTest(ProducerController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@Import({ProducerMapperImpl.class, ProducerService.class, BeanConfig.class, FileUtils.class, ProducerUtils.class})
class ProducerControllerTest {

    private static final String URL = "/v1/producers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private ProducerUtils producerUtils;

    @MockBean
    private ProducerData producerData;

    @SpyBean
    private ProducerHardCodedRepository repository;


    @BeforeEach
    void init() {

        BDDMockito.when(producerData.getProducers()).thenReturn(producerUtils.newProducerList());
    }


    @Test

    @DisplayName("findAll() returns a list with found producers when name is not null")
    @Order(1)
    void findAll_ReturnsFoundProducers_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourcesFile("producer/get-producer-null-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findAll() returns a list with found producers when name is not null")
    @Order(2)
    void findAll_ReturnsFoundProducers_WhenNameIsPassedAndFound() throws Exception {
        var name = "Ufotable";

        var response = fileUtils.readResourcesFile("producer/get-producer-ufotable-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", name)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() returns an empty list when no producer is found by name")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNoNameIsFound() throws Exception {
        var name = "x";

        var response = fileUtils.readResourcesFile("producer/get-producer-x-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", name)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("save() creates a producer")
    @Order(4)
    void save_CreatesAProducer_WhenSuccessful() throws Exception {

        var request = fileUtils.readResourcesFile("producer/post-request-producer-200.json");

        var response = fileUtils.readResourcesFile("producer/post-response-producer-201.json");

        var producerToSave = producerUtils.newProducerToSave();

        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(producerToSave);

        mockMvc.perform(MockMvcRequestBuilders.post(URL + "/post").content(request).header("x-api-version", "v1").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("update() Update a producer")
    @Order(5)
    void update_UpdateProducer_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourcesFile("producer/put-request-producer-200.json");

        mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("update() Updates NotFound when producer is not found")
    @Order(6)
    void update_ThrowsNotFound_WhenNotFoundProducer() throws Exception {
        var request = fileUtils.readResourcesFile("producer/put-request-producer-404.json");

        mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound()).andExpect(MockMvcResultMatchers.status().reason("Producer not found to be updated"));
    }

    @Test
    @DisplayName("delete() Remove a producer")
    @Order(7)
    void delete_RemovesProducer_WhenSuccessful() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1L)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("delete() Updates NotFound when producer is not found")
    @Order(8)
    void delete_ThrowsNotFound_WhenNotFoundProducer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", 999L)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound()).andExpect(MockMvcResultMatchers.status().reason("Producer not found to be deleted"));
    }

    @ParameterizedTest
    @MethodSource("postProducerBadRequestSourceFiles")
    @DisplayName("save() returns bad request when fields are incorrect")
    @Order(9)
    public void save_ReturnsBadRequest_WhenFieldsAreIncorrect(String fileName, List<String> errors) throws Exception {

        var request = fileUtils.readResourcesFile("producer/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL + "/post").content(request).header("x-api-version", "v1").contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);


    }

    private static Stream<Arguments> postProducerBadRequestSourceFiles() {

        var errorMessage = Collections.singletonList("The field 'name' is required");

        return Stream.of(Arguments.of("post-request-producer-blank-fields-400.json", errorMessage), Arguments.of("post-request-producer-empty-fields-400.json", errorMessage));
    }

    @ParameterizedTest
    @MethodSource("putProducerBadRequestSourceFiles")
    @DisplayName("update() returns bad request when fields are incorrect")
    @Order(10)
    public void update_ReturnsBadRequest_WhenFieldsAreIncorrect(String fileName, List<String> errors) throws Exception {

        var request = fileUtils.readResourcesFile("producer/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);


    }

    private static Stream<Arguments> putProducerBadRequestSourceFiles() {
        var errorMessage = Collections.singletonList("The field 'name' is required");

        return Stream.of(Arguments.of("put-request-producer-blank-fields-400.json", errorMessage), Arguments.of("put-request-producer-empty-fields-400.json", errorMessage));
    }

}