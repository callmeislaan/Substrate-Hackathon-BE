package com.backend.apiserver.security;

import com.backend.apiserver.exception.handler.JwtForbiddenHandler;
import com.backend.apiserver.exception.handler.JwtUnauthorizedHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/**
	 * PasswordEncoder
	 */
	private PasswordEncoder passwordEncoder;

	/**
	 * UserDetailsService
	 */
	private UserDetailsService userDetailsService;

	private JwtUnauthorizedHandler jwtUnauthorizedHandler;

	private JwtForbiddenHandler jwtForbiddenHandler;

	/**
	 * TokenFilter
	 */
	private AuthTokenFilter authTokenFilter;

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors().and().csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				.antMatchers(
						"/api/auth",
						"/api/register",
						"/api/admin/auth",
						"/api/activation**",
						"/api/active-user",
						"/api/public/**"
				).permitAll()
				.anyRequest().authenticated().and()
				.exceptionHandling().authenticationEntryPoint(jwtUnauthorizedHandler).and()
				.exceptionHandling().accessDeniedHandler(jwtForbiddenHandler);

		http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
	}

}
