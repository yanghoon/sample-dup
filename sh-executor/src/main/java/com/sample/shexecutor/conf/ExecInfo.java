package com.sample.shexecutor.conf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "info")
public class ExecInfo {
	private String bin = "./";    	
	private List<String> users = new ArrayList<>();
	
	public void setBin(String bin) {
		this.bin = bin;
	}
	public String getBin() {
		return bin;
	}

	public void setUsers(List<String> users){
		/*
		 * https://github.com/spring-projects/spring-boot/issues/501
		 */
		this.users = users;
	}
	public List<String> getUsers() {
		return users;
	}
	public List<String> getMaskedUsers() {
		// https://stackoverflow.com/questions/33100298/masking-of-email-address-in-java
		List<String> masked = new ArrayList<>();

		for(String email : users){
			masked.add( email.replaceAll("(?<=.{2}).(?=.*@)", "*") );
		}

		return masked;
	}
}