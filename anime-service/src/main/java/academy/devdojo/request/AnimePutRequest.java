package academy.devdojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnimePutRequest {
    @NotNull
    private Long id;
    @NotBlank(message = "The field 'name' is required")
    @Schema(example = "Name of Anime to update")
    private String name;
}
