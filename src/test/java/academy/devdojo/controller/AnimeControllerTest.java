package academy.devdojo.controller;


import academy.devdojo.commons.AnimeUtils;
import academy.devdojo.commons.FileUtils;
import academy.devdojo.repository.AnimeData;
import academy.devdojo.repository.AnimeHardCodedRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AnimeController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeControllerTest {

    private static final String URL = "/v1/animes/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private AnimeUtils animeUtils;

    @MockBean
    private AnimeData animeData;

    @SpyBean
    private AnimeHardCodedRepository repository;

    @BeforeEach
    void init() {

        BDDMockito.when(animeData.getAnimes()).thenReturn(animeUtils.newAnimeList());

    }

    @Test
    @DisplayName("findAll() Return all animes")
    @Order(1)
    void findAll_ReturnAllAnimes_WhenSuccessful() throws Exception {

        var response = fileUtils.readResourcesFile("anime/get-anime-null-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findById() Return anime when id exists")
    @Order(2)
    void findById_ReturnOptionalAnime_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourcesFile("anime/get-anime-by-id-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findById() Return ResponseStatusException when anime id does not exists")
    @Order(3)
    void findById_ReturnOptionalEmpty_WhenIdDoesNotExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 999L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Anime not found"));


    }

    @Test
    @DisplayName("findAll() Return a list with found animes when 'name' is not null")
    @Order(4)
    void findAll_ReturnAnimeList_WithFoundAnimes() throws Exception {

        var response = fileUtils.readResourcesFile("anime/get-anime-bleach-name-200.json");

        var name = "Bleach";

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() Return an empty List when no anime is not found by name")
    @Order(5)
    void findAll_ReturnAnEmptyList_WhenNoNameIsFound() throws Exception {

        var response = fileUtils.readResourcesFile("anime/get-anime-x-name-200.json");

        var name = "x";

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("save() Create a anime")
    @Order(6)
    void save_CreateAnime_WhenSuccessful() throws Exception {

        var response = fileUtils.readResourcesFile("anime/post-response-anime-201.json");

        var request = fileUtils.readResourcesFile("anime/post-request-anime-200.json");

        var animeToSave = animeUtils.newAnimeToSave();

        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(animeToSave);

        mockMvc.perform(MockMvcRequestBuilders.post(URL + "/post").content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("update() Update a Anime")
    @Order(7)
    void update_UpdateAAnime_WhenSuccessful() throws Exception {

        var request = fileUtils.readResourcesFile("anime/put-request-anime-200.json");

        mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("update() Updates ResponseStatusException when anime is not found")
    @Order(8)
    void update_ThrowsResponseStatusException_WhenNotFoundAnime() throws Exception {

        var request = fileUtils.readResourcesFile("anime/put-request-anime-404.json");

        mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Anime not found"));

    }


    @Test
    @DisplayName("delete() Remove a anime")
    @Order(9)
    void delete_RemovesAnime_WhenSuccessful() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("delete() throw ResponseStatusException no anime is found")
    @Order(10)
    void delete_ThrowsResponseStatusException_WhenNotFoundAnime() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", 9999L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Anime not found"));

    }

}