package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Producer {
    private Long id;
    private String name;

    @Getter
    private static List<Producer> producers = new ArrayList<>();
    static {
        Producer producer1 = new Producer(1L, "Mappa");
        Producer producer2 = new Producer(2L, "Kyoto Animation");
        Producer producer3 = new Producer(3L, "Mad House");
        producers.addAll(List.of(producer1, producer2, producer3));
    }
}
