package io.github.gotonode.gem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, proxyTargetClass = true)
@Order(0)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        // TODO: Disable this for production!
        httpSecurity.csrf().disable();

        httpSecurity.headers().frameOptions().sameOrigin();

        httpSecurity.authorizeRequests()
                .antMatchers("/h2-console", "/h2-console/**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.GET, "/debug", "/debug/**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/debug", "/debug/**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/delete", "/delete/**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/toggle", "/toggle/**").hasAnyAuthority("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/add").permitAll()
                .antMatchers(HttpMethod.GET, "/fetch").permitAll()
                .antMatchers(HttpMethod.GET, "/done").permitAll()
                .antMatchers(HttpMethod.GET, "/css", "/css/**").permitAll()
                .antMatchers(HttpMethod.GET, "/img", "/img/**").permitAll()
                .antMatchers(HttpMethod.GET, "/js", "/js/**").permitAll()
                .anyRequest().authenticated().and()
                .formLogin().permitAll().and()
                .logout().logoutSuccessUrl("/").permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {

        String userPassword;
        String adminPassword;

        if (System.getenv("ADMIN_PASSWORD") == null) {
            adminPassword = "dev";
        } else {
            adminPassword = System.getenv("ADMIN_PASSWORD");
        }

        if (System.getenv("USER_PASSWORD") == null) {
            userPassword = "dev";
        } else {
            userPassword = System.getenv("USER_PASSWORD");
        }

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password(adminPassword)
                .authorities("ADMIN")
                .build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password(userPassword)
                .authorities("USER")
                .build();

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(admin);
        manager.createUser(user);

        return manager;
    }
}
