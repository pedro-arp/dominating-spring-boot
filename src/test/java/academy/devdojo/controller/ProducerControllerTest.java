package academy.devdojo.controller;


import academy.devdojo.domain.Producer;
import academy.devdojo.repository.ProducerData;
import academy.devdojo.repository.ProducerHardCodedRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(ProducerController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProducerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProducerData producerData;

    @SpyBean
    private ProducerHardCodedRepository repository;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeEach
    void init() {

        var producer1 = Producer.builder().id(1L).name("Ufotable").createdAt(LocalDateTime.now()).build();

        var producer2 = Producer.builder().id(2L).name("Wit Studio").createdAt(LocalDateTime.now()).build();

        var producer3 = Producer.builder().id(3L).name("Studio Ghibli").createdAt(LocalDateTime.now()).build();

        var producers = new ArrayList<>(List.of(producer1, producer2, producer3));

        BDDMockito.when(producerData.getProducers()).thenReturn(producers);
    }


    @Test
    @DisplayName("findAll() returns a list with found producers when name is not null")
    @Order(1)
    void findAll_ReturnsFoundProducers_WhenSuccessful() throws Exception {
        var response = readResourcesFile("get-producer-null-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/producers")).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findAll() returns a list with found producers when name is not null")
    @Order(2)
    void findAll_ReturnsFoundProducers_WhenNameIsPassedAndFound() throws Exception {
        var name = "Ufotable";

        var response = readResourcesFile("get-producer-ufotable-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/producers").param("name", name)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() returns an empty list when no producer is found by name")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNoNameIsFound() throws Exception {
        var name = "x";

        var response = readResourcesFile("get-producer-x-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/producers").param("name", name)).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("save() creates a producer")
    @Order(4)
    void save_CreatesAProducer_WhenSuccessful() throws Exception {

        var request = readResourcesFile("post-request-producer-200.json");
        var response = readResourcesFile("post-response-producer-201.json");
        var producerToSave = Producer.builder()
                .id(99L)
                .name("MAPPA")
                .createdAt(LocalDateTime.now())
                .build();

        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(producerToSave);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/producers/post")
                        .content(request)
                        .header("x-api-version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("update() Update a producer")
    @Order(5)
    void update_UpdateProducer_WhenSuccessful() throws Exception {
        var request = readResourcesFile("put-request-producer-200.json");

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/producers").content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("update() Updates ResponseStatusException when producer is not found")
    @Order(6)
    void update_ThrowsResponseStatusException_WhenNotFoundProducer() throws Exception {
        var request = readResourcesFile("put-request-producer-404.json");

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/producers").content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Producer not found to be updated"));
    }

    @Test
    @DisplayName("delete() Remove a producer")
    @Order(7)
    void delete_RemovesProducer_WhenSuccessful() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/producers/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("delete() Updates ResponseStatusException when producer is not found")
    @Order(8)
    void delete_ThrowsResponseStatusException_WhenNotFoundProducer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/producers/{id}", 999L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Producer not found to be deleted"));
    }


    private String readResourcesFile(String fileName) throws Exception {
        var file = resourceLoader.getResource("classpath:%s".formatted(fileName)).getFile();
        return new String(Files.readAllBytes(file.toPath()));
    }
}