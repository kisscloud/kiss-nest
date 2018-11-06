package com.kiss.kissnest.feign;

import com.kiss.account.client.AccountClient;
import com.kiss.kissnest.config.FeignConfig;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient(value = "kiss-account",configuration = FeignConfig.class)
@Api(tags = "account", description = "账户服务调用")
public interface AccountServiceFeign extends AccountClient {
}
