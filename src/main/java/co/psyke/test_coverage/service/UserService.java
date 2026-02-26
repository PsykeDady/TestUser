package co.psyke.test_coverage.service;

import co.psyke.test_coverage.exception.InsufficientCreditsException;
import co.psyke.test_coverage.model.User;
import co.psyke.test_coverage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Create
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    // Read
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Update
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            user.setFullName(userDetails.getFullName());
            user.setActive(userDetails.getActive());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    // Delete
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }
    
    // Credits Management
    public Double getCredits(Long userId) {
        return userRepository.findById(userId)
                .map(User::getCredits)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
    
    public User spendCredits(Long userId, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        
        return userRepository.findById(userId).map(user -> {
            if (user.getCredits() < amount) {
                throw new InsufficientCreditsException(
                    String.format("Crediti insufficienti. Disponibili: %.2f, Richiesti: %.2f", 
                        user.getCredits(), amount)
                );
            }
            user.setCredits(user.getCredits() - amount);
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
    
    public User addCredits(Long userId, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        
        return userRepository.findById(userId).map(user -> {
            user.setCredits(user.getCredits() + amount);
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
}
