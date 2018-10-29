package com.kiss.kissnest.controller;

import com.kiss.account.input.ClientAuthorizationInput;
import com.kiss.kissnest.feign.ClientServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import output.ResultOutput;

@RestController
public class AuthController {

    @Autowired
    private ClientServiceFeign clientServiceFeign;

    @PostMapping("/code/login")
    public ResultOutput ClientAuthorization(@RequestBody ClientAuthorizationInput clientAuthorizationInput) {
        return clientServiceFeign.ClientAuthorization(clientAuthorizationInput);
    }
}
