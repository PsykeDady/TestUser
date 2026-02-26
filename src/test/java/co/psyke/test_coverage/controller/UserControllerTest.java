package co.psyke.test_coverage.controller;

import co.psyke.test_coverage.exception.InsufficientCreditsException;
import co.psyke.test_coverage.model.User;
import co.psyke.test_coverage.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private UserController userController;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    private User marioRossi;
    private User luigiBianchi;
    private User annaVerdi;
    private User saraBlu;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        
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
    
    // CRUD Endpoints Tests
    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(marioRossi);
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(marioRossi)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("mario_rossi"))
            .andExpect(jsonPath("$.email").value("mario.rossi@example.com"))
            .andExpect(jsonPath("$.credits").value(1500.00));
        
        verify(userService, times(1)).createUser(any(User.class));
    }
    
    @Test
    void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(marioRossi, luigiBianchi, annaVerdi, saraBlu);
        when(userService.getAllUsers()).thenReturn(users);
        
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(4))
            .andExpect(jsonPath("$[0].username").value("mario_rossi"))
            .andExpect(jsonPath("$[1].username").value("luigi_bianchi"))
            .andExpect(jsonPath("$[2].username").value("anna_verdi"))
            .andExpect(jsonPath("$[3].username").value("sara_blu"));
        
        verify(userService, times(1)).getAllUsers();
    }
    
    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(marioRossi));
        
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("mario_rossi"))
            .andExpect(jsonPath("$.fullName").value("Mario Rossi"));
        
        verify(userService, times(1)).getUserById(1L);
    }
    
    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound());
        
        verify(userService, times(1)).getUserById(999L);
    }
    
    @Test
    void testGetUserByUsername() throws Exception {
        when(userService.getUserByUsername("luigi_bianchi")).thenReturn(Optional.of(luigiBianchi));
        
        mockMvc.perform(get("/api/users/username/luigi_bianchi"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("luigi_bianchi"))
            .andExpect(jsonPath("$.credits").value(2000.00));
        
        verify(userService, times(1)).getUserByUsername("luigi_bianchi");
    }
    
    @Test
    void testGetUserByEmail() throws Exception {
        when(userService.getUserByEmail("anna.verdi@example.com")).thenReturn(Optional.of(annaVerdi));
        
        mockMvc.perform(get("/api/users/email/anna.verdi@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("anna.verdi@example.com"))
            .andExpect(jsonPath("$.credits").value(800.00));
        
        verify(userService, times(1)).getUserByEmail("anna.verdi@example.com");
    }
    
    @Test
    void testUpdateUser() throws Exception {
        User updatedUser = User.builder()
            .id(1L)
            .username("mario_updated")
            .email("mario.updated@example.com")
            .password("newpassword")
            .fullName("Mario Updated")
            .active(true)
            .credits(1500.00)
            .build();
        
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);
        
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("mario_updated"));
        
        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }
    
    @Test
    void testDeleteUser() throws Exception {
        when(userService.userExists(1L)).thenReturn(true);
        doNothing().when(userService).deleteUser(1L);
        
        mockMvc.perform(delete("/api/users/1"))
            .andExpect(status().isNoContent());
        
        verify(userService, times(1)).deleteUser(1L);
    }
    
    @Test
    void testDeleteUser_NotFound() throws Exception {
        when(userService.userExists(999L)).thenReturn(false);
        
        mockMvc.perform(delete("/api/users/999"))
            .andExpect(status().isNotFound());
        
        verify(userService, never()).deleteUser(anyLong());
    }
    
    // Credits Endpoints Tests
    @Test
    void testGetCredits() throws Exception {
        when(userService.getCredits(1L)).thenReturn(1500.00);
        
        mockMvc.perform(get("/api/users/1/credits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.credits").value(1500.00));
        
        verify(userService, times(1)).getCredits(1L);
    }
    
    @Test
    void testGetCredits_UserNotFound() throws Exception {
        when(userService.getCredits(999L)).thenThrow(new RuntimeException("User not found"));
        
        mockMvc.perform(get("/api/users/999/credits"))
            .andExpect(status().isNotFound());
        
        verify(userService, times(1)).getCredits(999L);
    }
    
    @Test
    void testSpendCredits_Success() throws Exception {
        User userAfterSpend = User.builder()
            .id(3L)
            .username("anna_verdi")
            .email("anna.verdi@example.com")
            .password("password123")
            .fullName("Anna Verdi")
            .active(true)
            .credits(700.00)
            .build();
        
        when(userService.spendCredits(3L, 100.00)).thenReturn(userAfterSpend);
        
        String requestBody = "{\"amount\": 100.0}";
        
        mockMvc.perform(post("/api/users/3/spend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(3))
            .andExpect(jsonPath("$.credits").value(700.00));
        
        verify(userService, times(1)).spendCredits(3L, 100.00);
    }
    
    @Test
    void testSpendCredits_InsufficientCredits() throws Exception {
        when(userService.spendCredits(3L, 900.00))
            .thenThrow(new InsufficientCreditsException("Crediti insufficienti. Disponibili: 800.00, Richiesti: 900.00"));
        
        String requestBody = "{\"amount\": 900.0}";
        
        mockMvc.perform(post("/api/users/3/spend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Crediti insufficienti. Disponibili: 800.00, Richiesti: 900.00"));
        
        verify(userService, times(1)).spendCredits(3L, 900.00);
    }
    
    @Test
    void testAddCredits() throws Exception {
        User userAfterAdd = User.builder()
            .id(2L)
            .username("luigi_bianchi")
            .email("luigi.bianchi@example.com")
            .password("password123")
            .fullName("Luigi Bianchi")
            .active(true)
            .credits(2500.00)
            .build();
        
        when(userService.addCredits(2L, 500.00)).thenReturn(userAfterAdd);
        
        String requestBody = "{\"amount\": 500.0}";
        
        mockMvc.perform(post("/api/users/2/add-credits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(2))
            .andExpect(jsonPath("$.credits").value(2500.00));
        
        verify(userService, times(1)).addCredits(2L, 500.00);
    }
}
