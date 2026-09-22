package com.amanda.todosimple.services;

import com.amanda.todosimple.models.User;
import com.amanda.todosimple.repositories.UserRepository;
import com.amanda.todosimple.services.exceptions.InvalidCredentialsException;
import com.amanda.todosimple.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "amanda", "senhaCriptografada");
    }

    @Test
    void findById_deveRetornarUsuario_quandoIdExiste() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("amanda", result.getUsername());
    }

    @Test
    void findById_deveLancarExcecao_quandoIdNaoExiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void create_deveCriptografarSenhaAntesDeSalvar() {
        User novoUsuario = new User(null, "amanda", "123456789");
        when(passwordEncoder.encode("123456789")).thenReturn("senhaCriptografada");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.create(novoUsuario);

        // confirma que o save recebeu a senha já criptografada, nunca o texto puro original
        verify(userRepository).save(argThat(u -> u.getPassword().equals("senhaCriptografada")));
    }

    @Test
    void login_deveRetornarUsuario_quandoCredenciaisCorretas() {
        when(userRepository.findByUsername("amanda")).thenReturn(user);
        when(passwordEncoder.matches("123456789", "senhaCriptografada")).thenReturn(true);

        User result = userService.login("amanda", "123456789");

        assertEquals(user, result);
    }

    @Test
    void login_deveLancarExcecao_quandoSenhaIncorreta() {
        when(userRepository.findByUsername("amanda")).thenReturn(user);
        when(passwordEncoder.matches("senhaErrada", "senhaCriptografada")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login("amanda", "senhaErrada"));
    }

    @Test
    void login_deveLancarExcecao_quandoUsuarioNaoExiste() {
        when(userRepository.findByUsername("naoexiste")).thenReturn(null);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login("naoexiste", "qualquerSenha"));
    }
}
