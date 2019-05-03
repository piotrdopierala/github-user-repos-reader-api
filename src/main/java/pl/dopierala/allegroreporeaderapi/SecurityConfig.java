package pl.dopierala.allegroreporeaderapi;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**")
                .permitAll()
                .and()
                .csrf()
                .disable()
                .addFilterBefore(corsFilter(), CsrfFilter.class);
    }

    @Bean
    public CorsFilter corsFilter() {
        String localOrigin = "http://localhost:4200";
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(localOrigin);
        config.addAllowedMethod(CorsConfiguration.ALL);
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**",config);
        return new CorsFilter(source);
    }
}
