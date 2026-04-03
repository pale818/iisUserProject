package hr.algebra.iisusers.users.repository;

import hr.algebra.iisusers.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
