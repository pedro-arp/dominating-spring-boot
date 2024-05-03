package academy.devdojo.repository;

import academy.devdojo.commons.AnimeUtils;
import academy.devdojo.domain.Anime;
import academy.devdojo.mapper.AnimeMapperImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@Import(AnimeMapperImpl.class)

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeHardCodedRepositoryTest {

    private List<Anime> animes;
    @Mock
    private AnimeData animeData;
    @InjectMocks
    private AnimeUtils animeUtils;
    @InjectMocks
    private AnimeHardCodedRepository repository;

    @BeforeEach
    void init() {

        animes = animeUtils.newAnimeList();

        BDDMockito.when(animeData.getAnimes()).thenReturn(animes);
    }

    @Test
    @DisplayName("findAll() must return a list of all anime")
    @Order(1)
    public void findAll_ReturnAnimes_WhenSuccessful() {
        var animes = repository.findAll();
        Assertions.assertThat(animes).hasSameElementsAs(this.animes);
    }

    @Test
    @DisplayName("findById() returns an anime when given Id")
    @Order(2)
    public void findById_ReturnAnimes_WhenSuccessful() {
        var animesOptional = repository.findById(2L);
        Assertions.assertThat(animesOptional).contains(this.animes.get(1));
    }

    @Test
    @DisplayName("findByName() must return a list of all anime")
    @Order(3)
    public void findByName_ReturnAllAnimes_WhenIsNull() {
        var animes = repository.findByName(null);
        Assertions.assertThat(animes).hasSameElementsAs(this.animes);

    }

    @Test
    @DisplayName("findByName() must return a list of all anime")
    @Order(4)
    public void findByName_ReturnAnimes_WhenSuccessful() {
        var animes = repository.findByName("One Punch Man");
        Assertions.assertThat(animes).hasSize(1).contains(this.animes.get(0));
    }

    @Test
    @DisplayName("findByName() returns empty list of animes when nothing is found")
    @Order(5)
    public void findByName_ReturnEmptyList_WhenNothingIsFound() {
        var animes = repository.findByName("XXXXXX");
        Assertions.assertThat(animes).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save() Create anime")
    @Order(6)
    public void save_CreateAnime_WhenSuccessful() {
        var animeToSave = animeUtils.newAnimeToSave();
        var anime = repository.save(animeToSave);
        Assertions.assertThat(anime).isEqualTo(animeToSave).hasNoNullFieldsOrProperties();
        var animes = repository.findAll();
        Assertions.assertThat(animes).contains(animeToSave);
    }

    @Test
    @DisplayName("delete() Delete anime")
    @Order(7)
    public void delete_RemoveAnime_WhenSuccessful() {
        var animeToDelete = this.animes.get(0);
        repository.delete(animeToDelete);
        Assertions.assertThat(this.animes).doesNotContain(animeToDelete);
    }

    @Test
    @DisplayName("update() Update anime")
    @Order(8)
    public void update_UpdateAnime_WhenSuccessful() {
        var animeToUpdate = this.animes.get(1);
        animeToUpdate.setName("Updating");
        repository.update(animeToUpdate);
        Assertions.assertThat(this.animes).contains(animeToUpdate);
        this.animes.stream().filter(animes -> animes.getId().equals(animeToUpdate.getId())).findFirst()
                .ifPresent(anime -> Assertions.assertThat(anime.getName()).isEqualTo(animeToUpdate.getName()));

    }


}