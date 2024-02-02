package academy.devdojo.service;

import academy.devdojo.domain.Anime;
import academy.devdojo.repository.AnimeHardCodedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeHardCodedRepository REPOSITORY;


    public Optional<Anime> findById(Long id) {
        return Optional.ofNullable(REPOSITORY.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found")));
    }

    public List<Anime> findAll(String name) {
        return REPOSITORY.findByName(name);
    }

    public Anime save(Anime anime) {
        return REPOSITORY.save(anime);
    }

    public void delete(Long id) {
        var anime = findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found to be deleted"));
        REPOSITORY.delete(anime);
    }

    public void update(Anime animeToUpdate) {
        findById(animeToUpdate.getId());
        REPOSITORY.update(animeToUpdate);
    }
}
