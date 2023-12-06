package exercices.springboot;

import domain.Anime;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "v1/animes/")
public class AnimeController {
    @GetMapping("list")
    public List<Anime> list(){
       return Anime.getAnimes();
    }
    @GetMapping("filter")
    public List<Anime> findByName(@RequestParam(required = false) String name){
        var animes = Anime.getAnimes();
        if (name == null) return animes;
        return animes.stream().filter(anime -> anime.getName().equalsIgnoreCase(name)).toList();
    }

    @GetMapping("{id}")
    public Anime findById(@PathVariable Long id){
        if (id == 0) return null;
        return Anime.getAnimes().stream().filter(anime -> anime.getId().equals(id)).findFirst().orElse(null);
    }
}


