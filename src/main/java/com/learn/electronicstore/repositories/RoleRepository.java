package com.learn.electronicstore.repositories;

import com.learn.electronicstore.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(String roleName);

}
