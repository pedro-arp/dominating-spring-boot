package academy.devdojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPostRequest {
    @NotBlank(message = "The field 'fistName' is required")
    @Schema(description = "User First Name", example = "Naruto")
    private String firstName;
    @NotBlank(message = "The field 'lastName' is required")
    @Schema(description = "User Last Name", example = "Uzumaki")
    private String lastName;
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "The email format is not valid")
    @Schema(description = "User Email", example = "naruto.uzumaki@hotmail.com")
    private String email;
    @NotBlank(message = "The field 'password' is required")
    private String password;

}
