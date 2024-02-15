package academy.devdojo.service;

import academy.devdojo.domain.Anime;
import academy.devdojo.repository.AnimeHardCodedRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeServiceTest {
    @InjectMocks
    private AnimeService service;
    @Mock
    private AnimeHardCodedRepository repository;

    private List<Anime> animes;

    @BeforeEach
    void init() {

        var anime1 = Anime.builder().id(1L).name("One Punch Man").build();

        var anime2 = Anime.builder().id(2L).name("Jujutsu Kaisen").build();

        var anime3 = Anime.builder().id(3L).name("Bleach").build();

        animes = new ArrayList<>(List.of(anime1, anime2, anime3));

    }

    @Test
    @DisplayName("findAll() Return all animes")
    @Order(1)
    void findAll_ReturnAllAnimes_WhenSuccessful() {

        BDDMockito.when(repository.findByName(null)).thenReturn(this.animes);
        var animes = service.findAll(null);
        Assertions.assertThat(animes).hasSameElementsAs(this.animes);
    }

    @Test
    @DisplayName("findAll() Return a list with found animes when 'name' is not null")
    @Order(2)
    void findAll_ReturnAnimeList_WithFoundAnimes() {
        var name = "One Punch Man";

        var animeFound = this.animes.stream().filter(anime -> anime.getName().equals(name)).toList();

        BDDMockito.when(repository.findByName(name)).thenReturn(animeFound);

        var animes = service.findAll(name);

        Assertions.assertThat(animes).hasSize(1).contains(animeFound.get(0));
    }

    @Test
    @DisplayName("findAll() Return an empty List when no anime is not found by name")
    @Order(3)
    void findAll_ReturnAnEmptyList_WhenNoNameIsFound() {

        var name = "x";

        BDDMockito.when(repository.findByName(name)).thenReturn(Collections.emptyList());

        var animes = service.findAll(name);

        Assertions.assertThat(animes).isNotNull().isEmpty();

    }

    @Test
    @DisplayName("findById() Return anime when id exists")
    @Order(4)
    void findById_ReturnOptionalAnime_WhenSuccessful() {

        var id = 2L;

        var animeFound = this.animes.get(1);

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(animeFound));

        var animes = service.findById(id);

        Assertions.assertThat(animes).isEqualTo(animeFound);

    }

    @Test
    @DisplayName("findById() Return ResponseStatusException when anime id does not exists")
    @Order(5)
    void findById_ReturnOptionalEmpty_WhenIdDoesNotExists() {
        var id = 2L;

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException().isThrownBy(() ->service.findById(id)).isInstanceOf(ResponseStatusException.class);

    }

    @Test
    @DisplayName("save() Create a anime")
    @Order(6)
    void save_CreateAnime_WhenSuccessful() {

        var animeToSave = Anime.builder().id(4L).name("Viewtiful Joe").build();

        BDDMockito.when(repository.save(animeToSave)).thenReturn(animeToSave);

        var animes = service.save(animeToSave);

        Assertions.assertThat(animes).isEqualTo(animeToSave).hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("delete() Remove a anime")
    @Order(7)
    void delete_RemovesAnime_WhenSuccessful() {

        var id = 3L;

        var animeToDelete = this.animes.get(2);

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(animeToDelete));

        BDDMockito.doNothing().when(repository).delete(animeToDelete);

        service.delete(id);

        Assertions.assertThatNoException().isThrownBy(() -> service.delete(id));

    }

    @Test
    @DisplayName("delete() throw ResponseStatusException no anime is found")
    @Order(8)
    void delete_ThrowsResponseStatusException_WhenNotFoundAnime() {

        var id = 5L;

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException().isThrownBy(() -> service.delete(id)).isInstanceOf(ResponseStatusException.class);

    }

    @Test
    @DisplayName("update() Update a Anime")
    @Order(9)
    void update_UpdateAAnime_WhenSuccessful() {

        var id = 2L;

        var animeToUpdate = this.animes.get(1);

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(animeToUpdate));

        BDDMockito.doNothing().when(repository).update(animeToUpdate);

        service.update(animeToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(animeToUpdate));


    }

    @Test
    @DisplayName("update() Updates ResponseStatusException when anime is not found")
    @Order(10)
    void update_ThrowsResponseStatusException_WhenNotFoundAnime() {
        var id = 2L;

        var animeToUpdate = this.animes.get(1);

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(animeToUpdate))
                .isInstanceOf(ResponseStatusException.class);
    }


}