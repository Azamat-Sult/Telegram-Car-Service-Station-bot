package ru.sult.azamat.telegramcarservicestationbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;
    private String username;
    private boolean isAdmin;

}