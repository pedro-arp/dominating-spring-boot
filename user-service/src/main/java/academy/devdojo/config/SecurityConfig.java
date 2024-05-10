package academy.devdojo.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Log4j2
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] WHITE_LIST = {"/swagger-ui/index.html", "v3/api-docs/**", "/swagger-ui/**", "/csrf"};

//    @Value("${user.username}")
//    private String usernameUser;
//    @Value("${user.password}")
//    private String passwordUser;
//    @Value("${admin.username}")
//    private String usernameAdmin;
//    @Value("${admin.password}")
//    private String passwordAdmin;
//    @Bean
//    public UserDetailsService userDetails(PasswordEncoder passwordEncoder) {
//        var user = User.withUsername(usernameUser)
//                .password(passwordEncoder.encode(passwordUser))
//                .roles("USER")
//                .build();
//        var admin = User.withUsername(usernameAdmin)
//                .password(passwordEncoder.encode(passwordAdmin))
//                .roles("ADMIN")
//                .build();//        return new InMemoryUserDetailsManager(user, admin); }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
//                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        //.requestMatchers(HttpMethod.GET, "v1/users/list").hasAuthority("ADMIN") -> present in Controller
                        .requestMatchers(HttpMethod.POST, "v1/users").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "v1/users/*").hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}
