package domain;

import java.util.List;

public class Anime {

    private  Long id;
    private  String name;

    public Anime(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<Anime> getAnimes(){
        Anime anime1 = new Anime(1L, "Naruto");
        Anime anime2 = new Anime(2L, "Drabon Ball");
        Anime anime3 = new Anime(3L, "One Piece");
        Anime anime4 = new Anime(4L, "Pokemon");
        return List.of(anime1, anime2, anime3, anime4);
    }
}
