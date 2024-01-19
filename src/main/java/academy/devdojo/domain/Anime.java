package academy.devdojo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Anime {

    private Long id;
    @JsonProperty(value = "full_name")
    private String name;
    @Getter
    private static List<Anime> animes = new ArrayList<>();
    static {
        Anime anime1 = new Anime(1L, "Naruto");
        Anime anime2 = new Anime(2L, "Dragon Ball");
        Anime anime3 = new Anime(3L, "One Piece");
        Anime anime4 = new Anime(4L, "Pokemon");
        animes.addAll(List.of(anime1, anime2, anime3, anime4));
    }
}
