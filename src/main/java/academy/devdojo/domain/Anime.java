package academy.devdojo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Anime {
    @EqualsAndHashCode.Include
    private Long id;
    @JsonProperty(value = "full_name")
    private String name;
    @Getter
    private static List<Anime> animes = new ArrayList<>();

    static {
        var anime1 = Anime.builder().id(1L).name("Naruto").build();
        var anime2 = Anime.builder().id(2L).name("Dragon Ball").build();
        var anime3 = Anime.builder().id(3L).name("One Piece").build();
        var anime4 = Anime.builder().id(4L).name("Pokemon").build();
        animes.addAll(List.of(anime1, anime2, anime3, anime4));
    }
}
