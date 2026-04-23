// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
public class ApitotalApplication {
	public static void main(final String[] args) {
		SpringApplication.run(ApitotalApplication.class, args);
	}
}
