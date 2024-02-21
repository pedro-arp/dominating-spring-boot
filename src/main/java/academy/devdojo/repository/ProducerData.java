package academy.devdojo.repository;

import academy.devdojo.domain.Producer;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class ProducerData {
    private final List<Producer> producers = new ArrayList<>();

    {
        var producer1 = Producer.builder().id(1L).name("MAPPA").createdAt(LocalDateTime.now()).build();
        var producer2 = Producer.builder().id(2L).name("Kyoto Animation").createdAt(LocalDateTime.now()).build();
        var producer3 = Producer.builder().id(3L).name("MadHouse").createdAt(LocalDateTime.now()).build();
        producers.addAll(List.of(producer1, producer2, producer3));
    }

}
