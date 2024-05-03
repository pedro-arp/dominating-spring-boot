package academy.devdojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPostResponse {
    @Schema(description = "User ID", example = "1")
    private Long id;
    @Schema(description = "User First Name", example = "Naruto")
    private String firstName;
    @Schema(description = "User Last Name", example = "Uzumaki")
    private String lastName;
    @Schema(description = "User Email", example = "naruto.uzumaki@hotmail.com")
    private String email;
}
