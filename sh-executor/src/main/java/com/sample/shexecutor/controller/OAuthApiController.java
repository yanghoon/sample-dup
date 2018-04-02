package com.sample.shexecutor.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//@RestController
@SuppressWarnings("unchecked")
public class OAuthApiController {
	private static String authorizationRequestBaseUri = "oauth2/authorization";

	@Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

	@GetMapping(name="auth/methods")
	public Object oauth(Model model) {
		Iterable<ClientRegistration> sites = (Iterable<ClientRegistration>) clientRegistrationRepository;

	    Map<String, String> urls = new HashMap<>();
	    for(ClientRegistration site : sites){
	      urls.put(site.getClientName(), authorizationRequestBaseUri + "/" + site.getRegistrationId());
	    }
	    
	    model.addAttribute("urls", urls);

	    return new HashMap<>(model.asMap());
	}
}
