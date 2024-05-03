package academy.devdojo.repository;

import academy.devdojo.commons.ProducerUtils;
import academy.devdojo.domain.Producer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProducerHardCodedRepositoryTest {

    private List<Producer> producers;
    @Mock
    private ProducerData producerData;
    @InjectMocks
    private ProducerUtils producerUtils;
    @InjectMocks
    private ProducerHardCodedRepository repository;

    @BeforeEach
    void init() {

        producers = producerUtils.newProducerList();

        BDDMockito.when(producerData.getProducers()).thenReturn(producers);
    }

    @Test
    @DisplayName("findAll() must return a list of all producers")
    @Order(1)
    void findAll_ReturnsAllProducers_WhenSuccessful() {

        var producers = repository.findAll();

        Assertions.assertThat(producers).hasSameElementsAs(this.producers);
    }

    @Test
    @DisplayName("findById() returns an producer when given Id")
    @Order(2)
    void findById_ReturnProducerById_WhenSuccessful() {

        var producerOptional = repository.findById(3L);

        Assertions.assertThat(producerOptional).contains(producers.get(2));
    }

    @Test
    @DisplayName("findByName() returns all Producers when name is null")
    @Order(3)
    void findByName_ReturnAllProducers_WhenNameIsNull() {

        var producers = repository.findByName(null);

        Assertions.assertThat(producers).hasSameElementsAs(this.producers);
    }

    @Test
    @DisplayName("findByName() returns list with filtered producers name is not null")
    @Order(4)
    void findByName_ReturnFilteredProducers_WhenNameIsNotNull() {

        var producers = repository.findByName("Ufotable");

        Assertions.assertThat(producers).hasSize(1).contains(this.producers.get(0));
    }

    @Test
    @DisplayName("findByName() returns empty list of producers when nothing is found")
    @Order(5)
    void findByName_ReturnEmptyList_WhenNothingIsFound() {

        var producers = repository.findByName("XXXX");

        Assertions.assertThat(producers).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save() Create a producer")
    @Order(6)
    void save_CreatesProducer_WhenSuccessful() {

        var producerToSave = producerUtils.newProducerToSave();

        var producer = repository.save(producerToSave);

        Assertions.assertThat(producer).isEqualTo(producerToSave).hasNoNullFieldsOrProperties();

        var producers = repository.findAll();

        Assertions.assertThat(producers).contains(producerToSave);
    }

    @Test
    @DisplayName("delete() Removes a producer")
    @Order(7)
    void delete_RemovesProducer_WhenSuccessful() {

        var producerToDelete = this.producers.get(0);

        repository.delete(producerToDelete);

        Assertions.assertThat(this.producers).doesNotContain(producerToDelete);
    }

    @Test
    @DisplayName("update() Update a producer")
    @Order(8)
    void update_UpdateProducer_WhenSuccessful() {

        var producerToUpdate = this.producers.get(0);

        producerToUpdate.setName("Test");

        repository.update(producerToUpdate);

        Assertions.assertThat(this.producers).contains(producerToUpdate);

        this.producers.stream().filter(producer -> producer.getId().equals(producerToUpdate.getId())).findFirst().ifPresent(producer -> Assertions.assertThat(producer.getName()).isEqualTo(producerToUpdate.getName()));
    }

}