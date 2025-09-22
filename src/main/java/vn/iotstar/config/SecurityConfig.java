package vn.iotstar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authz -> authz
				// Static resources - phải permit trước
				.requestMatchers("/css/**", "/js/**", "/images/**", "/video-files/**", "/avatars/**", "/uploads/**")
				.permitAll()

				// Public URLs - không cần đăng nhập
				.requestMatchers("/", "/login", "/register", "/logout").permitAll()
				.requestMatchers("/search", "/categories", "/categories/**").permitAll()
				.requestMatchers("/error").permitAll()

				// Admin URLs - chỉ ADMIN mới được truy cập (phải đặt trước /admin/**)
				.requestMatchers("/admin").hasRole("ADMIN").requestMatchers("/admin/**").hasRole("ADMIN")

				// User URLs - cần đăng nhập (USER hoặc ADMIN)
				.requestMatchers("/profile", "/profile/**").hasAnyRole("USER", "ADMIN")

				// Tất cả request khác cần đăng nhập
				.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").successHandler(authenticationSuccessHandler)
						.failureUrl("/login?error=true").usernameParameter("userName").permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true")
						.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll())
				.userDetailsService(userDetailsService)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
						.sessionFixation().migrateSession())
				.csrf(csrf -> csrf.csrfTokenRepository(
						new org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository()));

		return http.build();
	}
}
