package com.kiss.kissnest.controller;

import com.kiss.account.input.ClientAuthorizationInput;
import com.kiss.kissnest.feign.ClientServiceFeign;
import com.kiss.kissnest.input.LoginInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import output.ResultOutput;

@RestController
public class AuthController {

    @Autowired
    private ClientServiceFeign clientServiceFeign;

    @Value("${kiss.nest.clientId}")
    private String clientId;

    @Value("${kiss.nest.clientSecret}")
    private String clientSecret;

    @Value("${kiss.nest.clientExpired}")
    private String clientExpired;

    @PostMapping("/code/login")
    public ResultOutput ClientAuthorization(@RequestBody LoginInput loginInput) {

        ClientAuthorizationInput clientAuthorizationInput = new ClientAuthorizationInput();
        clientAuthorizationInput.setCode(loginInput.getAuthorizationCode());
        clientAuthorizationInput.setClientId(clientId);
        clientAuthorizationInput.setSecret(clientSecret);
        clientAuthorizationInput.setExpired(Long.valueOf(clientExpired));

        return clientServiceFeign.ClientAuthorization(clientAuthorizationInput);
    }
}
