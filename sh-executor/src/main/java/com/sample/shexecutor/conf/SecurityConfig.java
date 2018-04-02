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

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@SuppressWarnings("unused")
	private String[] permits = {"/", "/h2-console/**", "favicon.ico", "/login**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	/*
    	 * http://www.baeldung.com/spring-security-5-oauth2-login
    	 */
		http.authorizeRequests()
			.anyRequest().authenticated()
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
    		
    		System.out.println("login.user.name  = " + authentication.getName());
    		System.out.println("login.user.name  = " + profile.get("name"));
    		System.out.println("login.user.email = " + profile.get("email"));
    		
    		if( !info.getUsers().contains(profile.get("email")) )
    			throw new RuntimeException("Permission Denied.");
		}
    }
}