package academy.devdojo.repository;

import academy.devdojo.domain.Anime;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class AnimeData {
    private final List<Anime> animes = new ArrayList<>();

    {
        var anime1 = Anime.builder().id(1L).name("Naruto").build();
        var anime2 = Anime.builder().id(2L).name("Dragon Ball").build();
        var anime3 = Anime.builder().id(3L).name("One Piece").build();
        var anime4 = Anime.builder().id(4L).name("Pokemon").build();
        animes.addAll(List.of(anime1, anime2, anime3, anime4));
    }
}
