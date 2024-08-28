package academy.devdojo.response;

import java.util.List;

public record CepGetErrorResponse(String name, String message, String type, List<ErrorResponse> errors) {
}

record ErrorResponse(String name, String message, String service) {}
