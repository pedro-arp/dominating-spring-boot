package academy.devdojo.controller;


import academy.devdojo.domain.Producer;
import academy.devdojo.repository.ProducerData;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

    private List<Producer> producers;

    @BeforeEach
    void init() {

        var producer1 = Producer.builder().id(1L).name("Ufotable").createdAt(LocalDateTime.now()).build();

        var producer2 = Producer.builder().id(2L).name("Wit Studio").createdAt(LocalDateTime.now()).build();

        var producer3 = Producer.builder().id(3L).name("Studio Ghibli").createdAt(LocalDateTime.now()).build();

        producers = new ArrayList<>(List.of(producer1, producer2, producer3));

        BDDMockito.when(producerData.getProducers()).thenReturn(producers);
    }


    @Test
    @DisplayName("")
    @Order(1)
    void find() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/producers"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1L));
    }

}