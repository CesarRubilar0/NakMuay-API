package com.example.proyecto1spring.util;

import com.example.proyecto1spring.entity.Role;
import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.repository.RoleRepository;
import com.example.proyecto1spring.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;



@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(new Role("ADMIN"));
        }
        if (roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(new Role("USER"));
        }

        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setApellido("");
            admin.setEmail("admin@example.com");
            admin.setRut("00000000-0");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRol("ADMIN");
            userRepository.save(admin);
        }
    }
}
