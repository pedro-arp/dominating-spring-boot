package academy.devdojo.service;

import academy.devdojo.commons.CepUtils;
import academy.devdojo.config.BrasilApiConfigurationProperties;
import academy.devdojo.config.RestClientConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

@RestClientTest({BrasilApiService.class, BrasilApiConfigurationProperties.class, RestClientConfiguration.class, CepUtils.class, ObjectMapper.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BrasilApiServiceTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CepUtils cepUtils;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private BrasilApiService service;

    @Autowired
    private BrasilApiConfigurationProperties properties;


    @Order(1)
    @Test
    void findCep_ReturnsCepGetResponse_WhenSuccessful() throws JsonProcessingException {
        var cep = "00000-000";
        var cepGetResponse = cepUtils.newCepGetResponse();
        var jsonResponse = mapper.writeValueAsString(cepGetResponse);
        var requestTo = MockRestRequestMatchers.requestToUriTemplate(properties.baseUrl() + properties.uri(), cep);
        var withSuccess = MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON);
        server.expect(requestTo).andRespond(withSuccess);

        Assertions.assertThat(service.findCep(cep)).isNotNull().isEqualTo(cepGetResponse);
    }
}