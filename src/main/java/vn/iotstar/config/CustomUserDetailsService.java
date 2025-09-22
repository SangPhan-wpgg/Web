package vn.iotstar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("=== CustomUserDetailsService Debug ===");
		System.out.println("Loading user: " + username);

		User appUser = userService.findByUserName(username);
		System.out.println("User found: " + appUser);

		if (appUser == null) {
			System.out.println("User not found, throwing exception");
			throw new UsernameNotFoundException("User not found: " + username);
		}

		List<GrantedAuthority> authorities = new ArrayList<>();

		// Add role authority
		if (appUser.getRole() != null) {
			String roleName = appUser.getRole().getRoleName();
			authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
			System.out.println("Added role: ROLE_" + roleName);
		} else {
			// Default role if no role is set
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			System.out.println("Added default role: ROLE_USER");
		}

		System.out.println("Authorities: " + authorities);
		System.out.println("=== End CustomUserDetailsService Debug ===");

		return new org.springframework.security.core.userdetails.User(appUser.getUserName(), appUser.getPassword(),
				true, // enabled
				true, // accountNonExpired
				true, // credentialsNonExpired
				true, // accountNonLocked
				authorities);
	}
}
