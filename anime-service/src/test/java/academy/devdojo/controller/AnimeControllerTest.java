package academy.devdojo.controller;


import academy.devdojo.commons.AnimeUtils;
import academy.devdojo.commons.FileUtils;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.mapper.AnimeMapperImpl;
import academy.devdojo.service.AnimeService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(AnimeController.class)
@Import({AnimeMapperImpl.class, FileUtils.class, AnimeUtils.class, AnimeService.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeControllerTest {

    private static final String URL = "/v1/animes";
    private static final Long ID_FOUND = 3L;
    private static final Long ID_NOT_FOUND = 1000L;
    private static final String INVALID_NAME = "x";
    private static final String ANIME_NOT_FOUND = "Anime not found";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private AnimeUtils animeUtils;

    @MockBean
    private AnimeService service;


    @Test
    @DisplayName("list() Return all animes")
    @Order(1)
    public void list_ReturnAllAnimes_WhenSuccessful() throws Exception {

        var response = fileUtils.readResourcesFile("anime/get-anime-list-all-200.json");

        BDDMockito.when(service.findAll()).thenReturn(animeUtils.newAnimeList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/list"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("list() Return animes paginated")
    @Order(2)
    public void list_ReturnAnimesPaginated_WhenSuccessful() throws Exception {

        var response = fileUtils.readResourcesFile("anime/get-anime-paginated-200.json");

        var animes = animeUtils.newAnimeList();

        var pageRequest = PageRequest.of(0, animes.size());

        var pagedAnimes = new PageImpl<>(animes, pageRequest, 1);

        BDDMockito.when(service.findAll(BDDMockito.any(Pageable.class))).thenReturn(pagedAnimes);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/paginated"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }


    @Test
    @DisplayName("list() Return an empty List when no anime is not found by name")
    @Order(3)
    public void list_ReturnAnEmptyList_WhenNoNameIsFound() throws Exception {


        var response = fileUtils.readResourcesFile("anime/get-anime-x-name-200.json");

        BDDMockito.when(service.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/list").param("name", INVALID_NAME))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findById() Return anime when id exists")
    @Order(4)
    public void findById_ReturnOptionalAnime_WhenSuccessful() throws Exception {

        var response = fileUtils.readResourcesFile("anime/get-anime-by-id-200.json");

        var animeFound = animeUtils.animeFound();

        BDDMockito.when(service.findById(ID_FOUND)).thenReturn(animeFound);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", ID_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findById() Return NotFound when anime id does not exists")
    @Order(5)
    public void findById_ReturnOptionalEmpty_WhenIdDoesNotExists() throws Exception {

        var response = fileUtils.readResourcesFile("anime/anime-response-not-found-error-404.json");

        BDDMockito.when(service.findById(ArgumentMatchers.any())).thenThrow(new NotFoundException(ANIME_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", ID_NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));


    }

    @Test
    @DisplayName("findByName() Return a list with found animes when 'name' is not null")
    @Order(6)
    public void findByName_ReturnAnimeList_WithFoundAnimes() throws Exception {

        var animeFound = animeUtils.animeFound();

        var name = animeFound.getName();

        var response = fileUtils.readResourcesFile("anime/get-anime-bleach-name-200.json");


        BDDMockito.when(service.findByName(name)).thenReturn(animeFound);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/filter").param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }


    @Test
    @DisplayName("save() Create a anime")
    @Order(7)
    public void save_CreateAnime_WhenSuccessful() throws Exception {

        var response = fileUtils.readResourcesFile("anime/post-response-anime-201.json");

        var request = fileUtils.readResourcesFile("anime/post-request-anime-200.json");

        var animeToSave = animeUtils.newAnimeToSave();

        BDDMockito.when(service.save(ArgumentMatchers.any())).thenReturn(animeToSave);

        mockMvc.perform(MockMvcRequestBuilders.post(URL + "/post").content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("update() Update a Anime")
    @Order(8)
    public void update_UpdateAAnime_WhenSuccessful() throws Exception {

        var request = fileUtils.readResourcesFile("anime/put-request-anime-200.json");

        BDDMockito.doNothing().when(service).update(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("update() Updates NotFound when anime is not found")
    @Order(9)
    public void update_ThrowsNotFound_WhenNotFoundAnime() throws Exception {

        var response = fileUtils.readResourcesFile("anime/anime-response-not-found-error-404.json");

        var request = fileUtils.readResourcesFile("anime/put-request-anime-404.json");

        BDDMockito.doThrow(new NotFoundException(ANIME_NOT_FOUND)).when(service).update(ArgumentMatchers.any());


        mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }


    @Test
    @DisplayName("delete() Remove a anime")
    @Order(10)
    public void delete_RemovesAnime_WhenSuccessful() throws Exception {

        BDDMockito.doNothing().when(service).delete(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", ID_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("delete() throw NotFound no anime is found")
    @Order(11)
    public void delete_ThrowsNotFound_WhenNotFoundAnime() throws Exception {

        var response = fileUtils.readResourcesFile("anime/anime-response-not-found-error-404.json");

        BDDMockito.doThrow(new NotFoundException(ANIME_NOT_FOUND)).when(service).delete(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", ID_NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @ParameterizedTest
    @MethodSource("postAnimeBadRequestSourceFiles")
    @DisplayName("save() Returns bad request when fields are incorrect")
    @Order(12)
    public void save_ReturnsBadRequest_WhenFieldsAreIncorrect(String fileName, List<String> errors) throws Exception {

        var request = fileUtils.readResourcesFile("anime/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL + "/post")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);

    }

    private static Stream<Arguments> postAnimeBadRequestSourceFiles() {

        var errorMessage = Collections.singletonList("The field 'name' is required");

        return Stream.of(

                Arguments.of("post-request-anime-blank-fields-400.json", errorMessage),
                Arguments.of("post-request-anime-empty-fields-400.json", errorMessage));
    }

    @ParameterizedTest
    @MethodSource("putAnimeBadRequestSourceFiles")
    @DisplayName("update() Returns bad request when fields are incorrect")
    @Order(13)
    public void update_ReturnsBadRequest_WhenFieldsAreIncorrect(String fileName, List<String> errors) throws Exception {

        var request = fileUtils.readResourcesFile("anime/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URL).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);


    }

    private static Stream<Arguments> putAnimeBadRequestSourceFiles() {

        var errorMessage = Collections.singletonList("The field 'name' is required");

        return Stream.of(
                Arguments.of("put-request-anime-blank-fields-400.json", errorMessage),
                Arguments.of("put-request-anime-empty-fields-400.json", errorMessage));
    }


}