package vn.iotstar.service.impl;

import vn.iotstar.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public User findByUserName(String userName) {
		return userRepository.findByUserName(userName);
	}

	@Override
	public User findById(int id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public boolean existsByUserName(String userName) {
		return userRepository.existsByUserName(userName);
	}

	@Override
	public boolean existsByPhone(String phone) {
		return userRepository.existsByPhone(phone);
	}

}
