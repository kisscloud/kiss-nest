package com.kiss.kissnest.feign;

import com.kiss.account.client.ClientClient;
import com.kiss.kissnest.config.FeignConfig;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient(value = "kiss-account",configuration = FeignConfig.class)
@Api(tags = "client", description = "客户端服务调用")
public interface ClientServiceFeign extends ClientClient {
}
