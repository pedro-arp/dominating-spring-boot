package academy.devdojo.service;

import academy.devdojo.domain.Producer;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.ProducerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static academy.devdojo.util.Constants.PRODUCER_NOT_FOUND;

@Service
@RequiredArgsConstructor

public class ProducerService {

    private final ProducerRepository repository;

    public List<Producer> findAll() {
        return repository.findAll();
    }

    public Producer findById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCER_NOT_FOUND));
    }

    public Producer findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new NotFoundException(PRODUCER_NOT_FOUND));
    }

    public Producer save(Producer producer) {
        return repository.save(producer);
    }

    public void delete(Long id) {
        var producer = findById(id);

        repository.delete(producer);
    }

    public void update(Producer producerToUpdate) {
        findById(producerToUpdate.getId());

        repository.save(producerToUpdate);
    }


}
