package academy.devdojo.commons;

import academy.devdojo.domain.Anime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnimeUtils {

    public List<Anime> newAnimeList() {
        var anime1 = Anime.builder().id(1L).name("One Punch Man").build();

        var anime2 = Anime.builder().id(2L).name("Jujutsu Kaisen").build();

        var anime3 = Anime.builder().id(3L).name("Bleach").build();

        var anime4 = Anime.builder().id(4L).name("Pokemon").build();

        return new ArrayList<>(List.of(anime1, anime2, anime3, anime4));
    }

    public Anime newAnimeToSave() {
        return Anime.builder().id(99L).name("DeathNote").build();
    }

    public Anime animeFound() {
        return newAnimeList().get(2);
    }

}
