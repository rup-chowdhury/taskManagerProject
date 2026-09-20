package com.taskmanager.userservice.repo;

import com.taskmanager.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    List<User> findAllByOrderByIdAsc();
}
