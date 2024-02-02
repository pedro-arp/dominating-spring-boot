package academy.devdojo.repository;

import academy.devdojo.domain.Anime;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class AnimeHardCodedRepository {
    private static final List<Anime> ANIMES = new ArrayList<>();

    static {
        var anime1 = Anime.builder().id(1L).name("Naruto").build();
        var anime2 = Anime.builder().id(2L).name("Dragon Ball").build();
        var anime3 = Anime.builder().id(3L).name("One Piece").build();
        var anime4 = Anime.builder().id(4L).name("Pokemon").build();
        ANIMES.addAll(List.of(anime1, anime2, anime3, anime4));
    }

    public Optional<Anime> findById(Long id) {
        return ANIMES.stream().filter(anime -> anime.getId().equals(id)).findFirst();
    }

    public List<Anime> findByName(String name) {
        return name == null ? ANIMES :
                ANIMES.stream().filter(anime -> anime.getName().equalsIgnoreCase(name)).toList();
    }

    public Anime save(Anime anime) {
        ANIMES.add(anime);
        return anime;
    }

    public void delete(Anime anime) {
        ANIMES.remove(anime);
    }

    public void update(Anime anime) {
        save(anime);
        delete(anime);
    }
}
