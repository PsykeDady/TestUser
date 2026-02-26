package co.psyke.test_coverage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.database")
public class DatabaseInitializationProperties {
    
    private List<UserConfig> defaultUsers;
    
    @Data
    public static class UserConfig {
        private String username;
        private String email;
        private String password;
        private String fullName;
        private Boolean active;
        private Double credits;
    }
}
