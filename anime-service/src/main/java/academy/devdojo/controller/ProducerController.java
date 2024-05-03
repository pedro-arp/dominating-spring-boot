package academy.devdojo.controller;

import academy.devdojo.mapper.ProducerMapper;
import academy.devdojo.request.ProducerPostRequest;
import academy.devdojo.request.ProducerPutRequest;
import academy.devdojo.response.ProducerGetResponse;
import academy.devdojo.response.ProducerPostResponse;
import academy.devdojo.service.ProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/producers/", "v1/producers"})
@Log4j2
@RequiredArgsConstructor

public class ProducerController {

    private final ProducerMapper mapper;
    private final ProducerService producerService;

    @GetMapping
    public ResponseEntity<List<ProducerGetResponse>> list() {
        log.info("Request received to list all producers");

        var producers = producerService.findAll();

        var producersGetResponses = mapper.toProducerGetResponseList(producers);

        return ResponseEntity.ok(producersGetResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProducerGetResponse> findById(@PathVariable Long id) {
        log.info("Request received find producer by id '{}'", id);

        var producerFound = producerService.findById(id);

        var response = mapper.toProducerGetResponse(producerFound);

        return ResponseEntity.ok(response);
    }


    @GetMapping("filter")
    public ResponseEntity<ProducerGetResponse> findByName(@RequestParam(required = false) String name) {
        log.info("Request received to list all producers, param name '{}'", name);

        var producerFound = producerService.findByName(name);

        var producerGetResponse = mapper.toProducerGetResponse(producerFound);

        return ResponseEntity.ok(producerGetResponse);
    }

    @PostMapping(value = "post", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, headers = "x-api-version=v1")

    public ResponseEntity<ProducerPostResponse> save(@RequestBody @Valid ProducerPostRequest request) {

        var producer = mapper.toProducer(request);

        producer = producerService.save(producer);

        var response = mapper.toProducerPostResponse(producer);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.info("Request received to delete the producer by id '{}'", id);

        producerService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid ProducerPutRequest request) {

        log.info("Request received to delete the producer by id'{}'", request);

        var producerToUpdate = mapper.toProducer(request);

        producerService.update(producerToUpdate);

        return ResponseEntity.noContent().build();
    }


}
