package com.example.proyecto1spring.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String rut;

    private String nombre;

    private String apellido;

    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String rol;

    private boolean enabled;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creatAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;

    public Usuario() {
        this.enabled = true;
    }

    public Usuario(String nombre, String apellido, String password, String email, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.password = password;
        this.email = email;
        this.rol = rol;
        this.enabled = true;
    }

    @PrePersist
    protected void onCreate() {
        this.creatAt = new Date();
        this.updateAt = new Date();
        if (this.enabled == false) {
            this.enabled = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = new Date();
    }

    // getters
    public Long getId() {
        return id;
    }

    public String getRut() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }

    public Date getCreatAt() {
        return creatAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    // setters
    public void setRut(String rut) {
        this.rut = rut;
        this.updateAt = new Date();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        this.updateAt = new Date();
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
        this.updateAt = new Date();
    }

    public void setPassword(String password) {
        this.password = password;
        this.updateAt = new Date();
    }

    public void setEmail(String email) {
        this.email = email;
        this.updateAt = new Date();
    }

    public void setRol(String rol) {
        this.rol = rol;
        this.updateAt = new Date();
    }

    public boolean isEnabledField() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.updateAt = new Date();
    }

    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auth = new ArrayList<>();
        if (this.rol != null && !this.rol.isBlank()) {
            auth.add(new SimpleGrantedAuthority("ROLE_" + this.rol));
        }
        return auth;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
