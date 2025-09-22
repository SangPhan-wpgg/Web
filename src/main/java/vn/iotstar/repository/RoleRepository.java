package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	Role findByRoleName(String roleName);
}
