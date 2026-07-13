package com.bridgemind.backend.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

// Spring Data JPA repository — provides CRUD operations for the User entity.
// The custom findByEmail method is needed because email is the login identifier, not the UUID.
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
