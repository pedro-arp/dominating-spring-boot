package academy.devdojo.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Getter
@Setter
@Component
public class ApiError extends DefaultErrorAttributes {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        return super.getErrorAttributes(webRequest, options);
    }

    @Override
    @JsonIgnore
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


}
