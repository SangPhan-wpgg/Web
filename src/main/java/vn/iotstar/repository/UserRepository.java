package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUserName(String userName);

	boolean existsByEmail(String email);

	boolean existsByUserName(String userName);

	boolean existsByPhone(String phone);
}
