package co.psyke.test_coverage.initialization;

import co.psyke.test_coverage.config.DatabaseInitializationProperties;
import co.psyke.test_coverage.model.User;
import co.psyke.test_coverage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseInitializer implements ApplicationRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DatabaseInitializationProperties properties;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.count() == 0) {
            List<User> defaultUsers = properties.getDefaultUsers()
                .stream()
                .map(userConfig -> User.builder()
                    .username(userConfig.getUsername())
                    .email(userConfig.getEmail())
                    .password(userConfig.getPassword())
                    .fullName(userConfig.getFullName())
                    .active(userConfig.getActive() != null ? userConfig.getActive() : true)
                    .credits(userConfig.getCredits() != null ? userConfig.getCredits() : 0)
                    .build()
                )
                .collect(Collectors.toList());
            
            userRepository.saveAll(defaultUsers);
            System.out.println("✓ Database inizializzato con " + defaultUsers.size() + " utenti di default");
        }
    }
}
