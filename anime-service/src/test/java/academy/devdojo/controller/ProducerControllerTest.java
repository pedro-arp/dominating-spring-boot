package academy.devdojo.controller;


import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProducerUtils;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.mapper.ProducerMapperImpl;
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
@Import({ProducerMapperImpl.class, ProducerService.class, FileUtils.class, ProducerUtils.class})
class ProducerControllerTest {

    private static final String URL = "/v1/producers";
    private static final Long ID_FOUND = 1L;
    private static final Long ID_NOT_FOUND = 1000L;
    private static final String INVALID_NAME = "x";
    private static final String PRODUCER_NOT_FOUND = "Producer not found";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private ProducerUtils producerUtils;

    @MockBean
    private ProducerService service;


    @Test

    @DisplayName("list() returns a list with found producers when name is not null")
    @Order(1)
    void list_ReturnsFoundProducers_WhenSuccessful() throws Exception {

        var response = fileUtils.readResourcesFile("producer/get-producer-null-name-200.json");

        BDDMockito.when(service.findAll()).thenReturn(producerUtils.newProducerList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }


    @Test
    @DisplayName("list() returns an empty list when no producer is found by name")
    @Order(3)
    void list_ReturnsEmptyList_WhenNoNameIsFound() throws Exception {

        var response = fileUtils.readResourcesFile("producer/get-producer-x-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", INVALID_NAME)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findById() Return producer when id exists")
    @Order(4)
    public void findById_ReturnOptionalProducer_WhenSuccessful() throws Exception {


        var response = fileUtils.readResourcesFile("producer/get-producer-by-id-200.json");

        var animeFound = producerUtils.producerFound();

        BDDMockito.when(service.findById(ID_FOUND)).thenReturn(animeFound);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", ID_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findById() Return NotFound when producer id does not exists")
    @Order(5)
    public void findById_ReturnOptionalEmpty_WhenIdDoesNotExists() throws Exception {

        var response = fileUtils.readResourcesFile("producer/producer-response-not-found-error-404.json");

        BDDMockito.when(service.findById(ArgumentMatchers.any())).thenThrow(new NotFoundException(PRODUCER_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", ID_NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));


    }

    @Test
    @DisplayName("findByName() Return a list with found producers when 'name' is not null")
    @Order(6)
    public void findByName_ReturnProducerList_WithFoundProducers() throws Exception {

        var producerFound = producerUtils.producerFound();

        var name = producerFound.getName();

        var response = fileUtils.readResourcesFile("producer/get-producer-ufotable-name-200.json");


        BDDMockito.when(service.findByName(name)).thenReturn(producerFound);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/filter").param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("save() creates a producer")
    @Order(7)
    void save_CreatesAProducer_WhenSuccessful() throws Exception {

        var request = fileUtils.readResourcesFile("producer/post-request-producer-200.json");

        var response = fileUtils.readResourcesFile("producer/post-response-producer-201.json");

        var producerToSave = producerUtils.newProducerToSave();

        BDDMockito.when(service.save(ArgumentMatchers.any())).thenReturn(producerToSave);

        mockMvc.perform(MockMvcRequestBuilders.post(URL + "/post").content(request).header("x-api-version", "v1").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("update() Update a producer")
    @Order(8)
    void update_UpdateProducer_WhenSuccessful() throws Exception {

        var request = fileUtils.readResourcesFile("producer/put-request-producer-200.json");

        BDDMockito.doNothing().when(service).update(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("update() Updates NotFound when producer is not found")
    @Order(9)
    void update_ThrowsNotFound_WhenNotFoundProducer() throws Exception {

        var request = fileUtils.readResourcesFile("producer/put-request-producer-404.json");

        var response = fileUtils.readResourcesFile("producer/producer-response-not-found-error-404.json");

        BDDMockito.doThrow(new NotFoundException(PRODUCER_NOT_FOUND)).when(service).update(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("delete() Remove a producer")
    @Order(10)
    void delete_RemovesProducer_WhenSuccessful() throws Exception {

        BDDMockito.doNothing().when(service).delete(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", ID_FOUND)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("delete() throw NotFound when producer is not found")
    @Order(11)
    void delete_ThrowsNotFound_WhenNotFoundProducer() throws Exception {


        var response = fileUtils.readResourcesFile("producer/producer-response-not-found-error-404.json");

        BDDMockito.doThrow(new NotFoundException(PRODUCER_NOT_FOUND)).when(service).delete(ID_NOT_FOUND);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", ID_NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @ParameterizedTest
    @MethodSource("postProducerBadRequestSourceFiles")
    @DisplayName("save() returns bad request when fields are incorrect")
    @Order(12)
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
    @Order(13)
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