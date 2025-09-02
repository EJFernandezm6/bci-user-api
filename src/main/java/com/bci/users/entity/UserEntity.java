package com.bci.users.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable=false)
    private String name;


    @Column(nullable=false, unique=true)
    private String email;


    @Column(nullable=false)
    private String passwordHash;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PhoneEntity> phones = new ArrayList<>();


    private Instant created;
    private Instant modified;
    private Instant lastLogin;


    private String token;
    private boolean isActive;


    @PrePersist
    void onCreate(){
        created = lastLogin = Instant.now();
        modified = created;
        isActive = true;
    }


    @PreUpdate
    void onUpdate(){
        modified = Instant.now();
    }
}