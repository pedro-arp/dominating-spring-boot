package exercices.springboot;

import academy.devdojo.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "v1/animes/")
@Log4j2
public class AnimeController {
    @GetMapping
    public List<Anime> list() {
        log.info("Request received to list all animes");
        return Anime.getAnimes();
    }

    @GetMapping("filter")
    public List<Anime> findByName(@RequestParam(required = false) String name) {
        log.info("Request received to list all animes, param name '{}'", name);
        var animes = Anime.getAnimes();
        if (name == null) return animes;
        return animes.stream().filter(anime -> anime.getName().equalsIgnoreCase(name)).toList();
    }

    @GetMapping("{id}")
    public Anime findById(@PathVariable Long id) {
        log.info("Request received to find anime by id '{}'", id);

        if (id == 0) return null;
        return Anime.getAnimes().stream().filter(anime -> anime.getId().equals(id)).findFirst().orElse(null);
    }

    @PostMapping("post")
    public Anime save(@RequestBody Anime anime) {
        Anime lastObject = Anime.getAnimes().get(Anime.getAnimes().size() -1);
        anime.setId(lastObject.getId() + 1);
        Anime.getAnimes().add(anime);
        log.info("Saving anime '{}', id: '{}'", anime.getName(), anime.getId());
        return anime;
    }

}


