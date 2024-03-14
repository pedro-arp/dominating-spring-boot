package academy.devdojo.service;

import academy.devdojo.domain.Anime;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.AnimeHardCodedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeHardCodedRepository repository;

    public List<Anime> findAll(String name) {

        return repository.findByName(name);

    }

    public Anime findById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Anime not found"));
    }

    public Anime save(Anime anime) {
        return repository.save(anime);
    }

    public void delete(Long id) {

        var anime = findById(id);
        repository.delete(anime);

    }

    public void update(Anime animeToUpdate) {

        findById(animeToUpdate.getId());
        repository.update(animeToUpdate);
    }
}
