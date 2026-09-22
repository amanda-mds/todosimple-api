package com.amanda.todosimple.services;

import com.amanda.todosimple.models.User;
import com.amanda.todosimple.repositories.UserRepository;
import com.amanda.todosimple.services.exceptions.DataIntegrityException;
import com.amanda.todosimple.services.exceptions.InvalidCredentialsException;
import com.amanda.todosimple.services.exceptions.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findById(Long id){
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
                "Usuário não encontrado! Id: " + id));
    }

    @Transactional
    public User create(User obj) {
        obj.setId(null);
        obj.setPassword(passwordEncoder.encode(obj.getPassword()));
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj) {
        User newObj = findById(obj.getId());
        newObj.setPassword(passwordEncoder.encode(obj.getPassword()));
        return this.userRepository.save(newObj);
    }

    public void delete(Long id){
        findById(id);
        try{
            this.userRepository.deleteById(id);
        } catch (Exception e){
            throw new DataIntegrityException(
                    "Não é possível excluir pois há entidades relacionadas!");
        }
    }

    public User login(String username, String rawPassword) {
        User user = this.userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Usuário ou senha inválidos!");
        }
        return user;
    }

}
