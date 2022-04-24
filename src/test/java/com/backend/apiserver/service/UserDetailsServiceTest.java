package com.backend.apiserver.service;

import com.backend.apiserver.entity.Role;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.repository.UserRepository;
import com.backend.apiserver.service.impl.TransactionServiceImpl;
import com.backend.apiserver.service.impl.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	UserDetailsService userDetailsService = new UserDetailsServiceImpl(userRepository);

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void loadUserByUsername() {
		Role role = new Role();
		role.setName("ROLE_USER");
		User user = new User();
		user.setUsername("kafka2405");
		user.setPassword("kafka2405");
		user.setRole(role);
		Mockito.when(userRepository.findByUsernameOrEmailAndStatus(any(), any(), any())).thenReturn(user);
		UserDetails userDetails = userDetailsService.loadUserByUsername("kafka2405");
		assertEquals(userDetails.getUsername(),"kafka2405");
		assertEquals(userDetails.getPassword(),"kafka2405");
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getName());
		assertEquals(userDetails.getAuthorities().contains(grantedAuthority),true);
	}
}