package academy.devdojo.commons;

import academy.devdojo.domain.Producer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProducerUtils {

    public List<Producer> newProducerList() {

        var producer1 = Producer.builder().id(1L).name("Ufotable").createdAt(LocalDateTime.now()).build();

        var producer2 = Producer.builder().id(2L).name("Wit Studio").createdAt(LocalDateTime.now()).build();

        var producer3 = Producer.builder().id(3L).name("Studio Ghibli").createdAt(LocalDateTime.now()).build();

        return new ArrayList<>(List.of(producer1, producer2, producer3));
    }

    public Producer newProducerToSave() {
        return Producer.builder().id(99L).name("MAPPA").createdAt(LocalDateTime.now()).build();
    }

    public Producer producerFound() {
        return newProducerList().get(0);
    }
}
