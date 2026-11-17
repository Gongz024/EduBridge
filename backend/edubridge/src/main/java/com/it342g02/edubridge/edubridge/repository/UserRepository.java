package com.it342g02.edubridge.edubridge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.it342g02.edubridge.edubridge.entity.User;
import com.it342g02.edubridge.edubridge.entity.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
