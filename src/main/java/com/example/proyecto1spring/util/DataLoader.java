package com.example.proyecto1spring.util;

import com.example.proyecto1spring.entity.Plan;
import com.example.proyecto1spring.entity.Role;
import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.repository.PlanRepository;
import com.example.proyecto1spring.repository.RoleRepository;
import com.example.proyecto1spring.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(RoleRepository roleRepository, UserRepository userRepository, 
                      PlanRepository planRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
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

        // Crear planes si no existen
        if (planRepository.count() == 0) {
            Plan plan1 = new Plan();
            plan1.setNombre("Plan Básico");
            plan1.setDescripcion("Acceso a clases grupales 3 veces por semana");
            plan1.setPrecio(new BigDecimal("25000"));
            plan1.setDuracionMeses(1);
            plan1.setActivo(true);
            plan1.setCreatedAt(LocalDateTime.now());
            plan1.setUpdatedAt(LocalDateTime.now());
            planRepository.save(plan1);

            Plan plan2 = new Plan();
            plan2.setNombre("Plan Estándar");
            plan2.setDescripcion("Acceso ilimitado a clases grupales + 1 clase personalizada al mes");
            plan2.setPrecio(new BigDecimal("45000"));
            plan2.setDuracionMeses(3);
            plan2.setActivo(true);
            plan2.setCreatedAt(LocalDateTime.now());
            plan2.setUpdatedAt(LocalDateTime.now());
            planRepository.save(plan2);

            Plan plan3 = new Plan();
            plan3.setNombre("Plan Premium");
            plan3.setDescripcion("Acceso ilimitado + 4 clases personalizadas al mes + nutrición");
            plan3.setPrecio(new BigDecimal("75000"));
            plan3.setDuracionMeses(6);
            plan3.setActivo(true);
            plan3.setCreatedAt(LocalDateTime.now());
            plan3.setUpdatedAt(LocalDateTime.now());
            planRepository.save(plan3);

            Plan plan4 = new Plan();
            plan4.setNombre("Plan Anual");
            plan4.setDescripcion("Acceso total por 12 meses con descuento especial");
            plan4.setPrecio(new BigDecimal("500000"));
            plan4.setDuracionMeses(12);
            plan4.setActivo(true);
            plan4.setCreatedAt(LocalDateTime.now());
            plan4.setUpdatedAt(LocalDateTime.now());
            planRepository.save(plan4);

            System.out.println("✅ 4 planes creados exitosamente:");
            System.out.println("   - Plan Básico ($25,000 / 1 mes)");
            System.out.println("   - Plan Estándar ($45,000 / 3 meses)");
            System.out.println("   - Plan Premium ($75,000 / 6 meses)");
            System.out.println("   - Plan Anual ($500,000 / 12 meses)");
        } else {
            System.out.println("✅ Planes ya existen en la base de datos (" + planRepository.count() + " planes)");
        }
    }
}
