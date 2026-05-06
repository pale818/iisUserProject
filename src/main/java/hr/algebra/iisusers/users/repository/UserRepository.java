package hr.algebra.iisusers.users.repository;

import hr.algebra.iisusers.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data JPA generates the full implementation at runtime — no SQL needed for standard CRUD
public interface UserRepository extends JpaRepository<User, Long> {
}
