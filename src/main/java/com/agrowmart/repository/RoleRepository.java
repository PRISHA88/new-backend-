package com.agrowmart.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.agrowmart.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByName(String name);
}