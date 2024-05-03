package academy.devdojo.service;

import academy.devdojo.commons.ProducerUtils;
import academy.devdojo.domain.Producer;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.ProducerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProducerServiceTest {

    private static final Long ID_FOUND = 1L;
    private static final String INVALID_NAME = "x";
    private List<Producer> producers;
    @InjectMocks
    private ProducerService service;
    @InjectMocks
    private ProducerUtils producerUtils;
    @Mock
    private ProducerRepository repository;

    @BeforeEach
    void init() {

        producers = producerUtils.newProducerList();
    }

    @Test
    @DisplayName("list() Return all producers")
    @Order(1)
    void list_ReturnAllProducers_WhenSuccessful() {

        BDDMockito.when(repository.findAll()).thenReturn(this.producers);

        var producers = service.findAll();

        Assertions.assertThat(producers).hasSameElementsAs(this.producers);

    }

    @Test
    @DisplayName("list() Return a list with found producers when 'name' is not null")
    @Order(2)
    void list_ReturnFoundProducers_WhenNameIsNotNull() {

        var name = producerUtils.producerFound().getName();

        var producersFound = this.producers.stream().filter(producer -> producer.getName().equals(name)).toList();

        BDDMockito.when(repository.findAll()).thenReturn(producersFound);

        var producers = service.findAll();

        Assertions.assertThat(producers).hasSize(1).contains(producersFound.get(0));
    }

    @Test
    @DisplayName("list() Return an empty List when no producer is not found by name")
    @Order(3)
    void list_ReturnAnEmptyList_WhenNoNameIsFound() {

        BDDMockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        var producers = service.findAll();

        Assertions.assertThat(producers).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findById() Return optional producer when id exists")
    @Order(4)
    void findById_ReturnOptionalProducer_WhenSuccessful() {

        var producerFound = producerUtils.producerFound();

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.of(producerFound));

        var producers = service.findById(ID_FOUND);

        Assertions.assertThat(producers).isEqualTo(producerFound);
    }

    @Test
    @DisplayName("findById() Return optional empty when id does not exists")
    @Order(5)
    void findById_ReturnOptionalEmpty_WhenIdDoesNotExists() {


        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.empty());

        Assertions.assertThatException().isThrownBy(() -> service.findById(ID_FOUND)).isInstanceOf(NotFoundException.class);

    }

    @Test
    @DisplayName("findByName() Return Producer found when successful")
    void findByName_ReturnProducerFound_WhenSuccessful() {

        var producerFound = producers.get(2);

        var name = producerFound.getName();

        BDDMockito.when(repository.findByName(name)).thenReturn(Optional.of(producerFound));

        var producers = service.findByName(name);

        Assertions.assertThat(producers).isEqualTo(producerFound);
    }

    @Test
    @DisplayName("findByName() Return NotFoundException when producer not found")
    void findByName_ReturnNotFoundException_WhenProducerIsNotFound() {

        BDDMockito.when(repository.findByName(INVALID_NAME)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findByName(INVALID_NAME))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("save() Create a producer")
    @Order(6)
    void save_CreateProducer_WhenSuccessful() {

        var producerToSave = producerUtils.newProducerToSave();

        BDDMockito.when(repository.save(producerToSave)).thenReturn(producerToSave);

        var producer = service.save(producerToSave);

        Assertions.assertThat(producer).isEqualTo(producerToSave).hasNoNullFieldsOrProperties();

    }

    @Test
    @DisplayName("delete() Remove a producer")
    @Order(7)
    void delete_RemovesProducer_WhenSuccessful() {

        var producerToDelete = producerUtils.producerFound();

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.of(producerToDelete));

        BDDMockito.doNothing().when(repository).delete(producerToDelete);

        Assertions.assertThatNoException().isThrownBy(() -> service.delete(ID_FOUND));

    }

    @Test
    @DisplayName("delete() throw ResponseStatusException no producer is found")
    @Order(8)
    void delete_ThrowsResponseStatusException_WhenNotFoundProducer() {

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(ID_FOUND))
                .isInstanceOf(ResponseStatusException.class);

    }

    @Test
    @DisplayName("update() Update a Producer")
    @Order(9)
    void update_UpdateAProducer_WhenSuccessful() {

        var producerToUpdate = producerUtils.producerFound();

        producerToUpdate.setName("Update Test");

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.of(producerToUpdate));

        BDDMockito.when(repository.save(producerToUpdate)).thenReturn(producerToUpdate);

        service.update(producerToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(producerToUpdate));

    }

    @Test
    @DisplayName("update() Updates ResponseStatusException when producer is not found")
    @Order(10)
    void update_ThrowsResponseStatusException_WhenNotFoundProducer() {

        var producerToUpdate = this.producers.get(0);

        producerToUpdate.setName("Update Test");

        BDDMockito.when(repository.findById(ID_FOUND)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(producerToUpdate))
                .isInstanceOf(ResponseStatusException.class);

    }
}