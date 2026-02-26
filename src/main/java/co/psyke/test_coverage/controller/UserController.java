package co.psyke.test_coverage.controller;

import co.psyke.test_coverage.model.User;
import co.psyke.test_coverage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Create
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    // Read all
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Read by username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Read by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Update
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.userExists(id)) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Credits Endpoints
    @GetMapping("/{id}/credits")
    public ResponseEntity<CreditsResponse> getCredits(@PathVariable Long id) {
        try {
            Double credits = userService.getCredits(id);
            return ResponseEntity.ok(new CreditsResponse(id, credits));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/spend")
    public ResponseEntity<?> spendCredits(@PathVariable Long id, @RequestBody SpendCreditsRequest request) {
        try {
            User user = userService.spendCredits(id, request.getAmount());
            return ResponseEntity.ok(new CreditsResponse(id, user.getCredits()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/add-credits")
    public ResponseEntity<?> addCredits(@PathVariable Long id, @RequestBody SpendCreditsRequest request) {
        try {
            User user = userService.addCredits(id, request.getAmount());
            return ResponseEntity.ok(new CreditsResponse(id, user.getCredits()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // Helper classes
    public static class CreditsResponse {
        private Long userId;
        private Double credits;
        
        public CreditsResponse(Long userId, Double credits) {
            this.userId = userId;
            this.credits = credits;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public Double getCredits() {
            return credits;
        }
    }
    
    public static class SpendCreditsRequest {
        private Double amount;
        
        public SpendCreditsRequest() {}
        
        public Double getAmount() {
            return amount;
        }
        
        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
    
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
