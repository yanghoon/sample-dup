package com.sample.shexecutor.conf;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private String[] permits = {
		"favicon.ico",
		// -- swagger ui
		"/swagger-resources/**",
		"/swagger-ui.html",
		"/v2/api-docs",
		"/webjars/**",
	};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	/*
    	 * Login
    	 *   http://www.baeldung.com/spring-security-5-oauth2-login
    	 *   http://jojoldu.tistory.com/168
    	 * 
    	 * Logout
    	 *   https://docs.spring.io/spring-security/site/docs/current/reference/html/jc.html#jc-logout
    	 *   https://stackoverflow.com/a/24110187
    	 */
		http.authorizeRequests()
			.antMatchers(permits).permitAll()
			.anyRequest().authenticated()
			.and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
			.and().oauth2Login().successHandler(getSuccessHandler());
    }

    @Bean
	public SuccessHandler getSuccessHandler(){
		return new SuccessHandler();
	}

    public static class SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
    	@Autowired
    	private ExecInfo info;
    	
    	@Override
    	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
    		checkRegistration(authentication);

    		super.onAuthenticationSuccess(request, response, authentication);
    	}
    	
    	protected void checkRegistration(Authentication authentication) {
    		if( !(authentication instanceof OAuth2AuthenticationToken) ){
    			throw new RuntimeException("Support only oauth authentication.");
    		}
    		
    		OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
    		Map<String, Object> profile = auth.getPrincipal().getAttributes();
    		
    		System.out.println("auth.name       = " + authentication.getName());
    		System.out.println("auth.user.name  = " + profile.get("name"));
    		System.out.println("auth.user.email = " + profile.get("email"));
    		
    		if( !info.getUsers().contains(profile.get("email")) )
    			throw new RuntimeException("Permission Denied.");
		}
    }
}