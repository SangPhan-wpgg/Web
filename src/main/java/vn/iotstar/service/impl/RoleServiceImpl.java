package vn.iotstar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Role;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    public Role findById(int id) {
        return roleRepository.findById(id).orElse(null);
    }
    
    @Override
    public Role findByName(String name) {
        return roleRepository.findByRoleName(name);
    }
    
    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }
}
