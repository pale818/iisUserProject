package hr.algebra.iisusers.users.repository;

import hr.algebra.iisusers.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// This repository gives us built-in database methods like findAll,
// findById, save, and delete.
public interface UserRepository extends JpaRepository<User, Long> {
}
