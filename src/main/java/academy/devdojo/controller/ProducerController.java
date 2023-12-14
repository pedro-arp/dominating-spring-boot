package academy.devdojo.controller;

import domain.Producer;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "v1/producers/")
@Log4j2
public class ProducerController {
    @PostMapping(value = "post", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "x-api-version=v1")
    public ResponseEntity<Producer> save(@RequestBody Producer producer) {
        Producer lastObject = Producer.getProducers().get(Producer.getProducers().size() -1);
        producer.setId(lastObject.getId() + 1);
        Producer.getProducers().add(producer);
       return ResponseEntity.status(HttpStatus.CREATED).body(producer);
    }
}
