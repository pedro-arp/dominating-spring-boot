package academy.devdojo.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Log4j2
public class SecurityConfig {

    private static final String[] WHITE_LIST = {"/swagger-ui/index.html", "v3/api-docs/**", "/swagger-ui/**", "/csrf"};

    @Value("${user.username}")
    private String usernameUser;

    @Value("${user.password}")
    private String passwordUser;

    @Value("${admin.username}")
    private String usernameAdmin;

    @Value("${admin.password}")
    private String passwordAdmin;

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        var user = User.withUsername(usernameUser)
                .password(passwordEncoder.encode(passwordUser))
                .roles("USER")
                .build();

        var admin = User.withUsername(usernameAdmin)
                .password(passwordEncoder.encode(passwordAdmin))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth.requestMatchers(WHITE_LIST).permitAll().anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                //.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                //.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
