package academy.devdojo.controller;

import academy.devdojo.mapper.ProducerMapper;
import academy.devdojo.request.ProducerPostRequest;
import academy.devdojo.response.ProducerPostResponse;
import academy.devdojo.domain.Producer;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = {"v1/producers/", "v1/producers"})
@Log4j2
public class ProducerController {
    @PostMapping(value = "post", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, headers = "x-api-version=v1")

    public ResponseEntity<ProducerPostResponse> save(@RequestBody ProducerPostRequest request) {
        Producer lastObject = Producer.getProducers().getLast();

        var mapper = ProducerMapper.INSTANCE;
        var producer = mapper.toProducer(request);
        producer.setId(lastObject.getId() + 1);
        var response = mapper.toProducerPostResponse(producer);

        Producer.getProducers().add(producer);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
