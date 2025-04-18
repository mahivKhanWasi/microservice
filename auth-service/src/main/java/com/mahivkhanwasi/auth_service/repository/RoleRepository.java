package com.mahivkhanwasi.auth_service.repository;

import com.mahivkhanwasi.auth_service.model.Role;
import com.mahivkhanwasi.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
