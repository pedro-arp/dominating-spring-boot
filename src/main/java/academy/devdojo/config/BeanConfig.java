package academy.devdojo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import test.outside.Connection;

@Configuration
public class BeanConfig {
    @Bean(name = "MySQL")
    @Primary
    public Connection connectionMySql(){
        return new Connection("localhost::MySQL", "user", "password");
    }
    @Bean(name = "mongoDB")
    public Connection connectionMongoDb(){
        return new Connection("localhost::MongoDB", "user", "password");
    }
}
