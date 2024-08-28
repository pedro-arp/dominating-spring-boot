package academy.devdojo.commons;

import academy.devdojo.response.CepGetResponse;
import org.springframework.stereotype.Component;

@Component
public class CepUtils {
    public CepGetResponse newCepGetResponse() {
      return CepGetResponse.builder()
              .cep("00001-000")
              .city("São Paulo")
              .neighborhood("Vila Prudente")
              .state("SP")
              .service("viacep")
              .street("Rua de Asfalto")
              .build();
    }


}
