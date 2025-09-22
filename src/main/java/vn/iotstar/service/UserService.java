package vn.iotstar.service;

import vn.iotstar.entity.User;

public interface UserService {
	User findByUserName(String userName);
	
	User findById(int id);
	
	User save(User user);
	
	boolean existsByEmail(String email);
	
	boolean existsByUserName(String userName); 
	
	boolean existsByPhone(String phone);
}
