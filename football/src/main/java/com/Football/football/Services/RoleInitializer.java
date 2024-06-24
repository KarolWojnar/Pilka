package com.Football.football.Services;

import com.Football.football.Tables.Role;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createRoleIfNotFound("PLAYER");
        createRoleIfNotFound("COACH");
        createRoleIfNotFound("ANALYST");
    }

    @Transactional
    Role createRoleIfNotFound(String name) {
        Role role = entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        if (role == null) {
            role = new Role();
            role.setName(name);
            entityManager.persist(role);
        }

        return role;
    }
}