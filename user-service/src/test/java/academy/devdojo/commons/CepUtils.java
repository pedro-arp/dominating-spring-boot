package academy.devdojo.commons;

import academy.devdojo.response.CepErrorResponse;
import academy.devdojo.response.CepGetResponse;
import academy.devdojo.response.CepInnerErrorResponse;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public CepErrorResponse newCepErrorResponse() {
        var innerError = CepInnerErrorResponse.builder()
                .name("ServiceError")
                .message("CEP INVÁLIDO")
                .service("correios")
                .build();

        return CepErrorResponse.builder()
                .name("CepPromiseError")
                .message("Todos os serviços de CEP retornaram erro.")
                .type("service_error")
                .errors(List.of(innerError))
                .build();

    }


}
