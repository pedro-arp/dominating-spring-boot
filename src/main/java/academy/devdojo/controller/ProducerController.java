package academy.devdojo.controller;

import academy.devdojo.domain.Producer;
import academy.devdojo.mapper.ProducerMapper;
import academy.devdojo.request.ProducerPostRequest;
import academy.devdojo.request.ProducerPutRequest;
import academy.devdojo.response.ProducerGetResponse;
import academy.devdojo.response.ProducerPostResponse;
import academy.devdojo.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = {"v1/producers/", "v1/producers"})
@Log4j2
@RequiredArgsConstructor

public class ProducerController {

    private static final ProducerMapper MAPPER = ProducerMapper.INSTANCE;
    private final ProducerService PRODUCER_SERVICE;

    @GetMapping
    public ResponseEntity<List<ProducerGetResponse>> list(@RequestParam(required = false) String name) {
        log.info("Request received to list all producers");

        var producers = PRODUCER_SERVICE.findAll(name);

        var producersGetResponses = MAPPER.toProducerGetResponseList(producers);

        return ResponseEntity.ok(producersGetResponses);
    }

    @GetMapping("{id}")
    public Optional<Producer> findById(@PathVariable Long id) {
        log.info("Request received to found producer, param id: '{}'", id);

        var producerFound = PRODUCER_SERVICE.findById(id);

        return ResponseEntity.ok(producerFound).getBody();
    }

    @GetMapping("filter")
    public ResponseEntity<List<ProducerGetResponse>> findByName(@RequestParam(required = false) String name) {

        log.info("Request received to list all producers, param name '{}'", name);

        var producers = PRODUCER_SERVICE.findAll(name);

        var producerGetResponses = MAPPER.toProducerGetResponseList(producers);

        return ResponseEntity.ok(producerGetResponses);
    }

    @PostMapping(value = "post", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, headers = "x-api-version=v1")

    public ResponseEntity<ProducerPostResponse> save(@RequestBody ProducerPostRequest request) {

        var producer = MAPPER.toProducer(request);

        producer = PRODUCER_SERVICE.save(producer);

        var response = MAPPER.toProducerPostResponse(producer);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        log.info("Request received to delete the producer by id'{}'", id);

        PRODUCER_SERVICE.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody ProducerPutRequest request) {

        log.info("Request received to delete the producer by id'{}'", request);

        var producerToUpdate = MAPPER.toProducer(request);

        PRODUCER_SERVICE.update(producerToUpdate);

        return ResponseEntity.noContent().build();
    }
}
