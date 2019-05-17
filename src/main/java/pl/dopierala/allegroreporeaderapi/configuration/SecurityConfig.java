package pl.dopierala.allegroreporeaderapi.configuration;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**")
                .permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                .httpBasic()
                .and()
                .csrf()
                .disable()
                .addFilterBefore(corsFilter(), CsrfFilter.class);
    }

    @Bean
    public CorsFilter corsFilter() {
        final String localOrigin = "http://localhost:4200";
        final String remoteFrontOrigin = "https://repo-reader-client.herokuapp.com";
        final String remoteAPIOrigin = "https://repo-reader-api.herokuapp.com";
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(localOrigin);
        config.addAllowedOrigin(remoteFrontOrigin);
        config.addAllowedOrigin(remoteAPIOrigin);
        config.addAllowedMethod(CorsConfiguration.ALL);
        config.addAllowedHeader("authorization");
        config.addAllowedHeader("content-type");
        config.addAllowedHeader("x-auth-token");
        config.setExposedHeaders(Arrays.asList("x-auth-token"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**",config);
        return new CorsFilter(source);
    }
}
