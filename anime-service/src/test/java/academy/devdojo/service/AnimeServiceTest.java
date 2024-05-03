package academy.devdojo.service;

import academy.devdojo.commons.AnimeUtils;
import academy.devdojo.domain.Anime;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.AnimeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeServiceTest {
    private static final Long ID_FOUND = 3L;
    private static final String INVALID_NAME = "x";
    private List<Anime> animes;
    @InjectMocks
    private AnimeService service;
    @InjectMocks
    private AnimeUtils animeUtils;
    @Mock
    private AnimeRepository repository;


    @BeforeEach
    void init() {

        animes = animeUtils.newAnimeList();

    }

    @Test
    @DisplayName("findAll() Return all animes")
    @Order(1)
    void findAll_ReturnAllAnimes_WhenSuccessful() {

        BDDMockito.when(repository.findAll()).thenReturn(this.animes);

        var animes = service.findAll();

        Assertions.assertThat(animes).hasSameElementsAs(this.animes);
    }

    @Test
    @DisplayName("findAll() Return all animes paginated")
    @Order(1)
    void findAll_ReturnAllAnimesPaginated_WhenSuccessful() {


        var pageRequest = PageRequest.of(0, this.animes.size());

        var pagedAnimes = new PageImpl<>(this.animes, pageRequest, 1);

        BDDMockito.when(repository.findAll(BDDMockito.any(Pageable.class))).thenReturn(pagedAnimes);

        var animesFound = service.findAll(pageRequest);

        Assertions.assertThat(animesFound).hasSameElementsAs(this.animes);
    }

    @Test
    @DisplayName("findAll() Return a list with found animes when 'name' is not null")
    @Order(2)
    void findAll_ReturnAnimeList_WithFoundAnimes() {
        var name = animeUtils.animeFound().getName();

        var animeFound = this.animes.stream().filter(anime -> anime.getName().equals(name)).toList();

        BDDMockito.when(repository.findAll()).thenReturn(animeFound);

        var animes = service.findAll();

        Assertions.assertThat(animes).hasSize(1).contains(animeUtils.animeFound());
    }

    @Test
    @DisplayName("findAll() Return an empty List when no anime is not found by name")
    @Order(3)
    void findAll_ReturnAnEmptyList_WhenNoNameIsFound() {

        BDDMockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        var animes = service.findAll();

        Assertions.assertThat(animes).isNotNull().isEmpty();

    }

    @Test
    @DisplayName("findById() Return anime when id exists")
    @Order(4)
    void findById_ReturnOptionalAnime_WhenSuccessful() {

        var animeFound = animeUtils.animeFound();

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.of(animeFound));

        var animes = service.findById(ID_FOUND);

        Assertions.assertThat(animes).isEqualTo(animeFound);

    }

    @Test
    @DisplayName("findById() Return NotFoundException when anime id does not exists")
    @Order(5)
    void findById_ReturnNotFoundException_WhenIdDoesNotExists() {

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.empty());

        Assertions.assertThatException().isThrownBy(() -> service.findById(ID_FOUND)).isInstanceOf(NotFoundException.class);

    }

    @Test
    @DisplayName("findByName() Return anime found when successful")
    void findByName_ReturnAnimeFound_WhenSuccessful() {

        var animeFound = animes.get(2);

        var name = animeFound.getName();

        BDDMockito.when(repository.findByName(name)).thenReturn(Optional.of(animeFound));

        var animes = service.findByName(name);

        Assertions.assertThat(animes).isEqualTo(animeFound);
    }

    @Test
    @DisplayName("findByName() Return NotFoundException when anime not found")
    void findByName_ReturnNotFoundException_WhenAnimeIsNotFound() {

        BDDMockito.when(repository.findByName(INVALID_NAME)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findByName(INVALID_NAME))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("save() Create a anime")
    @Order(6)
    void save_CreateAnime_WhenSuccessful() {

        var animeToSave = animeUtils.newAnimeToSave();

        BDDMockito.when(repository.save(animeToSave)).thenReturn(animeToSave);

        var animes = service.save(animeToSave);

        Assertions.assertThat(animes).isEqualTo(animeToSave).hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("delete() Remove a anime")
    @Order(7)
    void delete_RemovesAnime_WhenSuccessful() {


        var animeToDelete = animeUtils.animeFound();

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.of(animeToDelete));

        BDDMockito.doNothing().when(repository).delete(animeToDelete);

        service.delete(ID_FOUND);

        Assertions.assertThatNoException().isThrownBy(() -> service.delete(ID_FOUND));

    }

    @Test
    @DisplayName("delete() throw RNotFoundException no anime is found")
    @Order(8)
    void delete_ThrowsNotFoundException_WhenNotFoundAnime() {

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.empty());

        Assertions.assertThatException().isThrownBy(() -> service.delete(ID_FOUND)).isInstanceOf(NotFoundException.class);

    }

    @Test
    @DisplayName("update() Update an Anime")
    @Order(9)
    void update_UpdateAnime_WhenSuccessful() {

        var animeToUpdate = animeUtils.animeFound();

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.of(animeToUpdate));

        BDDMockito.when(repository.save(animeToUpdate)).thenReturn(animeToUpdate);

        service.update(animeToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(animeToUpdate));


    }

    @Test
    @DisplayName("update() Updates NotFoundException when anime is not found")
    @Order(10)
    void update_ThrowsNotFoundException_WhenNotFoundAnime() {

        var animeToUpdate = animeUtils.animeFound();

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(animeToUpdate))
                .isInstanceOf(NotFoundException.class);
    }


}