package com.kiss.kissnest.controller;

import com.kiss.account.input.ClientAuthorizationInput;
import com.kiss.kissnest.feign.ClientServiceFeign;
import com.kiss.kissnest.input.LoginInput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import output.ResultOutput;

@RestController
@Api(tags = "Auth", description = "授权相关接口")
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
    @ApiOperation(value = "登录")
    public ResultOutput ClientAuthorization(@RequestBody LoginInput loginInput) {

        ClientAuthorizationInput clientAuthorizationInput = new ClientAuthorizationInput();
        clientAuthorizationInput.setCode(loginInput.getAuthorizationCode());
        clientAuthorizationInput.setClientId(clientId);
        clientAuthorizationInput.setSecret(clientSecret);
        clientAuthorizationInput.setExpired(Long.valueOf(clientExpired));
        ResultOutput authInfo = clientServiceFeign.ClientAuthorization(clientAuthorizationInput);

        return authInfo;
    }
}
