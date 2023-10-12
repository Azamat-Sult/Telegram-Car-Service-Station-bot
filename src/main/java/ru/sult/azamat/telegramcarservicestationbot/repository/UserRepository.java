package ru.sult.azamat.telegramcarservicestationbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sult.azamat.telegramcarservicestationbot.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}