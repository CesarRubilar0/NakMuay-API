package com.example.proyecto1spring.service.impl;

import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.repository.UserRepository;
import com.example.proyecto1spring.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    //implements es para heredar metodos de una interfaz, donde se usan los metodos tales como estan definidos en la interfaz
    //tags de anotaciones de springboot como: @Service indican que esta clase es un servicio gestionado por el contenedor de springboot
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Usuario createUser(Usuario user, String roleName) {
        java.util.Objects.requireNonNull(user, "user no puede ser nulo");
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }
        if (userRepository.existsByRut(user.getRut())) {
            throw new IllegalArgumentException("RUT ya existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRol(roleName);
        return userRepository.save(user);
    }

    @Override
    public List<Usuario> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        java.util.Objects.requireNonNull(id, "id no puede ser nulo");
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        java.util.Objects.requireNonNull(id, "id no puede ser nulo");
        userRepository.deleteById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Usuario toggleEnabled(Long id) {
        java.util.Objects.requireNonNull(id, "id no puede ser nulo");
        Optional<Usuario> u = userRepository.findById(id);
        if (u.isPresent()) {
            Usuario user = u.get();
            boolean current = user.isEnabled();
            user.setEnabled(!current);
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("Usuario no encontrado");
    }

    @Override
    @Transactional
    public Usuario updateUser(Long id, Usuario userData) {
        java.util.Objects.requireNonNull(id, "id no puede ser nulo");
        java.util.Objects.requireNonNull(userData, "userData no puede ser nulo");
        Optional<Usuario> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        Usuario u = opt.get();
        // Verificar cambios de email/rut para evitar duplicados
        if (userData.getEmail() != null && !userData.getEmail().equals(u.getEmail()) && userRepository.existsByEmail(userData.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }
        if (userData.getRut() != null && !userData.getRut().equals(u.getRut()) && userRepository.existsByRut(userData.getRut())) {
            throw new IllegalArgumentException("RUT ya existe");
        }
        u.setNombre(userData.getNombre());
        u.setApellido(userData.getApellido());
        u.setEmail(userData.getEmail());
        u.setRut(userData.getRut());
        u.setRol(userData.getRol());
        // Si se proporciona nueva contraseña, actualizarla (codificada)
        if (userData.getPassword() != null && !userData.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(userData.getPassword()));
        }
        return userRepository.save(u);
    }

    @Override
    public Usuario findByEmailAsUsuario(String email) {
        java.util.Objects.requireNonNull(email, "email no puede ser nulo");
        return userRepository.findByEmail(email).orElse(null);
    }
}
