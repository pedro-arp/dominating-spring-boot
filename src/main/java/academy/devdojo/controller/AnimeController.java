package academy.devdojo.controller;

import academy.devdojo.domain.Anime;
import academy.devdojo.mapper.AnimeMapper;
import academy.devdojo.request.AnimePostRequest;
import academy.devdojo.request.AnimePutRequest;
import academy.devdojo.response.AnimeGetResponse;
import academy.devdojo.response.AnimePostResponse;
import academy.devdojo.service.AnimeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "v1/animes/")
@Log4j2
public class AnimeController {
    private static final AnimeMapper MAPPER = AnimeMapper.INSTANCE;
    private final AnimeService ANIME_SERVICE;

    public AnimeController() {
        ANIME_SERVICE = new AnimeService();
    }

    @GetMapping
    public ResponseEntity<List<AnimeGetResponse>> list(@RequestParam(required = false) String name) {

        log.info("Request received to list all animes");

        var animes = ANIME_SERVICE.findAll(name);

        var animesGetResponses = MAPPER.toAnimeGetResponseList(animes);

        return ResponseEntity.ok(animesGetResponses);
    }

    @GetMapping("{id}")
    public Optional<Anime> findById(@PathVariable Long id) {
        log.info("Request received to find anime by id '{}'", id);

        var animeFound = ANIME_SERVICE.findById(id);

        return ResponseEntity.ok(animeFound).getBody();

    }

    @GetMapping("filter")
    public ResponseEntity<List<AnimeGetResponse>> findByName(@RequestParam(required = false) String name) {
        log.info("Request received to list all animes, param name '{}'", name);
        var animes = ANIME_SERVICE.findAll(name);

        var animeGetResponses = MAPPER.toAnimeGetResponseList(animes);

        return ResponseEntity.ok(animeGetResponses);
    }

    @PostMapping("post")
    public ResponseEntity<AnimePostResponse> save(@RequestBody AnimePostRequest request) {

        log.info("Request received save anime '{}'", request);

        var anime = MAPPER.toAnime(request);

        anime = ANIME_SERVICE.save(anime);

        var response = MAPPER.toAnimePostResponse(anime);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Request received to delete the anime by id'{}'", id);

        ANIME_SERVICE.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody AnimePutRequest request) {
        log.info("Request received to delete the anime by id'{}'", request);

        var animeToUpdate = MAPPER.toAnime(request);

        ANIME_SERVICE.update(animeToUpdate);

        return ResponseEntity.noContent().build();
    }

}