package academy.devdojo.controller;


import academy.devdojo.domain.Anime;
import academy.devdojo.repository.AnimeData;
import academy.devdojo.repository.AnimeHardCodedRepository;
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
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(AnimeController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnimeData animeData;

    @Autowired
    private ResourceLoader resourceLoader;

    @SpyBean
    private AnimeHardCodedRepository repository;

    @BeforeEach
    void init() {

        var anime1 = Anime.builder().id(1L).name("One Punch Man").build();

        var anime2 = Anime.builder().id(2L).name("Jujutsu Kaisen").build();

        var anime3 = Anime.builder().id(3L).name("Bleach").build();

        var anime4 = Anime.builder().id(4L).name("Pokemon").build();

        var animes = new ArrayList<>(List.of(anime1, anime2, anime3, anime4));
        BDDMockito.when(animeData.getAnimes()).thenReturn(animes);

    }

    @Test
    @DisplayName("findAll() Return all animes")
    @Order(1)
    void findAll_ReturnAllAnimes_WhenSuccessful() throws Exception {

        var response = readResourcesFile("anime/get-anime-null-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findById() Return anime when id exists")
    @Order(2)
    void findById_ReturnOptionalAnime_WhenSuccessful() throws Exception {
        var response = readResourcesFile("anime/get-anime-by-id-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes/{id}",1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findById() Return ResponseStatusException when anime id does not exists")
    @Order(3)
    void findById_ReturnOptionalEmpty_WhenIdDoesNotExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes/{id}",999L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Anime not found"));


    }

    @Test
    @DisplayName("findAll() Return a list with found animes when 'name' is not null")
    @Order(3)
    void findAll_ReturnAnimeList_WithFoundAnimes() throws Exception {

        var response = readResourcesFile("anime/get-anime-bleach-name-200.json");

        var name = "Bleach";

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes/").param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() Return an empty List when no anime is not found by name")
    @Order(4)
    void findAll_ReturnAnEmptyList_WhenNoNameIsFound() throws Exception {

        var response = readResourcesFile("anime/get-anime-x-name-200.json");

        var name = "x";

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/animes/").param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("save() Create a anime")
    @Order(5)
    void save_CreateAnime_WhenSuccessful() throws Exception {

        var response = readResourcesFile("anime/post-response-anime-201.json");

        var request = readResourcesFile("anime/post-request-anime-200.json");

        var animeToSave = Anime.builder().id(99L).name("DeathNote").build();

        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(animeToSave);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/animes/post").content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("update() Update a Anime")
    @Order(6)
    void update_UpdateAAnime_WhenSuccessful() throws Exception {

        var request = readResourcesFile("anime/put-request-anime-200.json");

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/animes/").content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("update() Updates ResponseStatusException when anime is not found")
    @Order(7)
    void update_ThrowsResponseStatusException_WhenNotFoundAnime() throws Exception {

        var request = readResourcesFile("anime/put-request-anime-404.json");

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/animes/").content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Anime not found"));

    }


    @Test
    @DisplayName("delete() Remove a anime")
    @Order(8)
    void delete_RemovesAnime_WhenSuccessful() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/animes/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("delete() throw ResponseStatusException no anime is found")
    @Order(9)
    void delete_ThrowsResponseStatusException_WhenNotFoundAnime() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/animes/{id}", 9999L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Anime not found"));

    }

    private String readResourcesFile(String fileName) throws Exception {

        var file = resourceLoader.getResource("classpath:%s".formatted(fileName)).getFile();
        return new String(Files.readAllBytes(file.toPath()));

    }


}