package com.amanda.todosimple.services;

import com.amanda.todosimple.models.Task;
import com.amanda.todosimple.models.User;
import com.amanda.todosimple.repositories.TaskRepository;
import com.amanda.todosimple.services.exceptions.DataIntegrityException;
import com.amanda.todosimple.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User(1L, "amanda", "senhaCriptografada");
        task = new Task(1L, user, "Estudar Spring Boot");
    }

    @Test
    void findById_deveRetornarTarefa_quandoIdExiste() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.findById(1L);

        assertNotNull(result);
        assertEquals("Estudar Spring Boot", result.getDescription());
    }

    @Test
    void findById_deveLancarExcecao_quandoIdNaoExiste() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> taskService.findById(99L));
    }

    @Test
    void findAllByUserId_deveRetornarListaDeTarefasDoUsuario() {
        when(taskRepository.findByUser_Id(1L)).thenReturn(Collections.singletonList(task));

        List<Task> result = taskService.findAllByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(task, result.get(0));
    }

    @Test
    void create_deveVincularUsuarioCorretoESalvarTarefa() {
        Task novaTarefa = new Task(null, new User(1L, null, null), "Nova tarefa");
        when(userService.findById(1L)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.create(novaTarefa);

        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void delete_deveLancarDataIntegrityException_quandoFalharExclusao() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doThrow(new RuntimeException()).when(taskRepository).deleteById(1L);

        assertThrows(DataIntegrityException.class, () -> taskService.delete(1L));
    }
}
