package in.rbihub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    @Value("${spring.application.apiUser}")
    private String apiUser;

    @Value("${spring.application.apiPassword}")
    private String apiPassword;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(apiUser).password(passwordEncoder().encode(apiPassword)).roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http    .csrf().disable().authorizeRequests()
                //.authorizeRequests()
                .antMatchers("/actuator/prometheus").permitAll()
                .antMatchers("/actuator/health").permitAll()
                .antMatchers(HttpMethod.GET,"/landowner").permitAll()
                .antMatchers(HttpMethod.GET,"/TNSOutput").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    } 

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
