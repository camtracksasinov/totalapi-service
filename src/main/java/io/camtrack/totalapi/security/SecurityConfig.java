// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	protected void configure(final HttpSecurity http) throws Exception {
		((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) ((HttpSecurity) http.csrf().disable()).authorizeRequests()
				.anyRequest()).permitAll();
	}
}
