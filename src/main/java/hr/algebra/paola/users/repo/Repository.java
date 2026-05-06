package hr.algebra.paola.users.repo;

import hr.algebra.paola.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Repository extends JpaRepository<User, Long> {
}
