package academy.devdojo.service;

import academy.devdojo.domain.Producer;
import academy.devdojo.repository.ProducerHardCodedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class ProducerService {

    private final ProducerHardCodedRepository REPOSITORY;

    public List<Producer> findAll(String name) {
        return REPOSITORY.findByName(name);
    }

    public Optional<Producer> findById(Long id) {
        return REPOSITORY.findById(id);
    }

    public Producer save(Producer producer) {
        return REPOSITORY.save(producer);
    }

    public void delete(Long id) {
        var producer = findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producer not found to be deleted"));
        REPOSITORY.delete(producer);
    }

    public void update(Producer producerToUpdate) {
        var producer = findById(producerToUpdate.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producer not found to be updated"));
        producerToUpdate.setCreatedAt(producer.getCreatedAt());
        REPOSITORY.update(producerToUpdate);
    }
}
