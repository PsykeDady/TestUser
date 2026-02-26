package co.psyke.test_coverage.service;

import co.psyke.test_coverage.exception.InsufficientCreditsException;
import co.psyke.test_coverage.model.User;
import co.psyke.test_coverage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    private User marioRossi;
    private User luigiBianchi;
    private User annaVerdi;
    private User saraBlu;
    
    @BeforeEach
    void setUp() {
        // Dati dal file di configurazione application.yml
        marioRossi = User.builder()
            .id(1L)
            .username("mario_rossi")
            .email("mario.rossi@example.com")
            .password("password123")
            .fullName("Mario Rossi")
            .active(true)
            .credits(1500.00)
            .build();
        
        luigiBianchi = User.builder()
            .id(2L)
            .username("luigi_bianchi")
            .email("luigi.bianchi@example.com")
            .password("password123")
            .fullName("Luigi Bianchi")
            .active(true)
            .credits(2000.00)
            .build();
        
        annaVerdi = User.builder()
            .id(3L)
            .username("anna_verdi")
            .email("anna.verdi@example.com")
            .password("password123")
            .fullName("Anna Verdi")
            .active(true)
            .credits(800.00)
            .build();
        
        saraBlu = User.builder()
            .id(5L)
            .username("sara_blu")
            .email("sara.blu@example.com")
            .password("password123")
            .fullName("Sara Blu")
            .active(true)
            .credits(3000.00)
            .build();
    }
    
    // CRUD Tests
    @Test
    void testCreateUser() {
        when(userRepository.save(marioRossi)).thenReturn(marioRossi);
        
        User result = userService.createUser(marioRossi);
        
        assertNotNull(result);
        assertEquals("mario_rossi", result.getUsername());
        assertEquals("mario.rossi@example.com", result.getEmail());
        verify(userRepository, times(1)).save(marioRossi);
    }
    
    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(marioRossi));
        
        Optional<User> result = userService.getUserById(1L);
        
        assertTrue(result.isPresent());
        assertEquals("mario_rossi", result.get().getUsername());
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        Optional<User> result = userService.getUserById(999L);
        
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(999L);
    }
    
    @Test
    void testGetUserByUsername() {
        when(userRepository.findByUsername("luigi_bianchi")).thenReturn(Optional.of(luigiBianchi));
        
        Optional<User> result = userService.getUserByUsername("luigi_bianchi");
        
        assertTrue(result.isPresent());
        assertEquals("luigi_bianchi", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("luigi_bianchi");
    }
    
    @Test
    void testGetUserByEmail() {
        when(userRepository.findByEmail("anna.verdi@example.com")).thenReturn(Optional.of(annaVerdi));
        
        Optional<User> result = userService.getUserByEmail("anna.verdi@example.com");
        
        assertTrue(result.isPresent());
        assertEquals("anna.verdi@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("anna.verdi@example.com");
    }
    
    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(marioRossi, luigiBianchi, annaVerdi, saraBlu);
        when(userRepository.findAll()).thenReturn(users);
        
        List<User> result = userService.getAllUsers();
        
        assertEquals(4, result.size());
        assertEquals("mario_rossi", result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void testUpdateUser() {
        User updatedUser = User.builder()
            .id(1L)
            .username("mario_updated")
            .email("mario.updated@example.com")
            .password("newpassword")
            .fullName("Mario Updated")
            .active(true)
            .credits(1500.00)
            .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(marioRossi));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        
        User result = userService.updateUser(1L, updatedUser);
        
        assertEquals("mario_updated", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testUpdateUser_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(999L, marioRossi);
        });
    }
    
    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);
        
        verify(userRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(999L)).thenReturn(false);
        
        assertTrue(userService.userExists(1L));
        assertFalse(userService.userExists(999L));
        verify(userRepository, times(2)).existsById(any());
    }
    
    // Credits Tests
    @Test
    void testGetCredits() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(marioRossi));
        
        Double credits = userService.getCredits(1L);
        
        assertEquals(1500.00, credits);
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetCredits_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            userService.getCredits(999L);
        });
    }
    
    @Test
    void testSpendCredits_Success() {
        User userWithCredits = User.builder()
            .id(1L)
            .username("mario_rossi")
            .email("mario.rossi@example.com")
            .password("password123")
            .fullName("Mario Rossi")
            .active(true)
            .credits(1500.00)
            .build();
        
        User userAfterSpend = User.builder()
            .id(1L)
            .username("mario_rossi")
            .email("mario.rossi@example.com")
            .password("password123")
            .fullName("Mario Rossi")
            .active(true)
            .credits(1400.00)
            .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithCredits));
        when(userRepository.save(any(User.class))).thenReturn(userAfterSpend);
        
        User result = userService.spendCredits(1L, 100.00);
        
        assertEquals(1400.00, result.getCredits());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testSpendCredits_InsufficientCredits() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(annaVerdi)); // credits = 800
        
        InsufficientCreditsException exception = assertThrows(
            InsufficientCreditsException.class,
            () -> userService.spendCredits(3L, 900.00)
        );
        
        assertTrue(exception.getMessage().contains("Crediti insufficienti"));
        verify(userRepository, times(1)).findById(3L);
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void testSpendCredits_InvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.spendCredits(1L, -100.00);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.spendCredits(1L, 0.00);
        });
    }
    
    @Test
    void testSpendCredits_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            userService.spendCredits(999L, 100.00);
        });
    }
    
    @Test
    void testAddCredits_Success() {
        User userBefore = User.builder()
            .id(2L)
            .username("luigi_bianchi")
            .email("luigi.bianchi@example.com")
            .password("password123")
            .fullName("Luigi Bianchi")
            .active(true)
            .credits(2000.00)
            .build();
        
        User userAfter = User.builder()
            .id(2L)
            .username("luigi_bianchi")
            .email("luigi.bianchi@example.com")
            .password("password123")
            .fullName("Luigi Bianchi")
            .active(true)
            .credits(2500.00)
            .build();
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(userBefore));
        when(userRepository.save(any(User.class))).thenReturn(userAfter);
        
        User result = userService.addCredits(2L, 500.00);
        
        assertEquals(2500.00, result.getCredits());
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testAddCredits_InvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.addCredits(1L, -50.00);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.addCredits(1L, 0.00);
        });
    }
    
    @Test
    void testAddCredits_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            userService.addCredits(999L, 100.00);
        });
    }
}
