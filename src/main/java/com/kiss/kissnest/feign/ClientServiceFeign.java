package com.kiss.kissnest.feign;

import com.kiss.account.client.ClientClient;
import com.kiss.kissnest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient(value = "kiss-account",configuration = FeignConfig.class)
public interface ClientServiceFeign extends ClientClient {
}
