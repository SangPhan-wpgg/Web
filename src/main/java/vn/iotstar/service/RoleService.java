package vn.iotstar.service;

import vn.iotstar.entity.Role;

public interface RoleService {
	Role findById(int id);

	Role findByName(String name);

	Role save(Role role);
}
