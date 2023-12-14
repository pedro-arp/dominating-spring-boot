package academy.devdojo.controller;

import academy.devdojo.request.ProducerPostRequest;
import academy.devdojo.response.ProducerPostResponse;
import domain.Producer;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;


@RestController
@RequestMapping(path = "v1/producers/")
@Log4j2
public class ProducerController {
    @PostMapping(value = "post", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "x-api-version=v1")

    public ResponseEntity<ProducerPostResponse> save(@RequestBody ProducerPostRequest request) {
        Producer lastObject = Producer.getProducers().get(Producer.getProducers().size() - 1);
        var producer = Producer.builder().name(request.getName()).id(lastObject.getId() + 1).createdAt(LocalDateTime.now()).build();
        Producer.getProducers().add(producer);

        var response = ProducerPostResponse.builder()
                .id(producer.getId())
                .name(producer.getName())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
