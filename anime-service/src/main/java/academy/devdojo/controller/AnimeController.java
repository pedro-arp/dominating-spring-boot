package academy.devdojo.controller;

import academy.devdojo.mapper.AnimeMapper;
import academy.devdojo.request.AnimePostRequest;
import academy.devdojo.request.AnimePutRequest;
import academy.devdojo.response.AnimeGetResponse;
import academy.devdojo.response.AnimePostResponse;
import academy.devdojo.service.AnimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "v1/animes")
@Log4j2
@RequiredArgsConstructor
public class AnimeController {
    private final AnimeMapper mapper;

    private final AnimeService animeService;

    @GetMapping("list")
    public ResponseEntity<List<AnimeGetResponse>> list() {

        log.info("Request received to list all animes");

        var animes = animeService.findAll();

        var animesGetResponses = mapper.toAnimeGetResponsesList(animes);

        return ResponseEntity.ok(animesGetResponses);
    }

    @GetMapping("paginated")
    public ResponseEntity<Page<AnimeGetResponse>> list(@ParameterObject Pageable pageable) {

        log.info("Request received to list all animes");

        var pageAnimeGetResponse = animeService.findAll(pageable).map(mapper::toAnimeGetResponse);

        return ResponseEntity.ok(pageAnimeGetResponse);

    }

    @GetMapping("{id}")
    public ResponseEntity<AnimeGetResponse> findById(@PathVariable Long id) {
        log.info("Request received find anime by id '{}'", id);
        var animeFound = animeService.findById(id);

        var response = mapper.toAnimeGetResponse(animeFound);

        return ResponseEntity.ok(response);
    }

    @GetMapping("filter")
    public ResponseEntity<AnimeGetResponse> findByName(@RequestParam(required = false) String name) {
        log.info("Request received to list all animes, param name '{}'", name);

        var animeFound = animeService.findByName(name);

        var animeGetResponses = mapper.toAnimeGetResponse(animeFound);

        return ResponseEntity.ok(animeGetResponses);
    }

    @PostMapping("post")
    public ResponseEntity<AnimePostResponse> save(@RequestBody @Valid AnimePostRequest request) {

        log.info("Request received save anime '{}'", request);

        var anime = mapper.toAnime(request);

        anime = animeService.save(anime);

        var response = mapper.toAnimePostResponse(anime);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Request received to delete the anime by id'{}'", id);

        animeService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid AnimePutRequest request) {
        log.info("Request received to delete the anime by id'{}'", request);

        var animeToUpdate = mapper.toAnime(request);

        animeService.update(animeToUpdate);

        return ResponseEntity.noContent().build();
    }


}