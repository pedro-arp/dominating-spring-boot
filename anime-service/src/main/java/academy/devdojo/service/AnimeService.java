package academy.devdojo.service;

import academy.devdojo.domain.Anime;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static academy.devdojo.util.Constants.ANIME_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository repository;

    public List<Anime> findAll() {

        return repository.findAll();

    }

    public Page<Anime> findAll(Pageable pageable) {

        return repository.findAll(pageable);

    }

    public Anime findById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(ANIME_NOT_FOUND));
    }

    public Anime findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new NotFoundException(ANIME_NOT_FOUND));


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
        repository.save(animeToUpdate);
    }


}
