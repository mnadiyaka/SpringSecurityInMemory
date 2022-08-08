package SprSecurity.security;

import SprSecurity.auth.ApplicationUser;
import SprSecurity.auth.ApplicationUserServiceImpl;
import SprSecurity.auth.UserRepository;
import SprSecurity.jwt.JwtConfig;
import SprSecurity.jwt.JwtTokenVerifier;
import SprSecurity.jwt.JwtUsernameAndPasswordAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    private final ApplicationUserServiceImpl applicationUserServiceImpl;

    private final SecretKey secretKey;

    private final JwtConfig jwtConfig;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserServiceImpl applicationUserServiceImpl, SecretKey secretKey, JwtConfig jwtConfig) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserServiceImpl = applicationUserServiceImpl;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthFilter(authenticationManager(), jwtConfig, secretKey))
                .addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfig), JwtUsernameAndPasswordAuthFilter.class)
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/students/*").hasRole(ApplicationUserRoles.STUDENT.name())
/*              //Using this instead of annotations in controllers
                .antMatchers(HttpMethod.DELETE, "/managing/**").hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT, "/managing/**").hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, "/managing/**").hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.GET, "/managing/**").hasAnyRole(ApplicationUserRoles.ADMIN.name(), ApplicationUserRoles.ADMINTRAINEE.name())
*/
                .anyRequest()
                .authenticated()

/*              //Form log in
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/courses", true)
                .passwordParameter("pass")//by default password, can be changed if in html is different name; same goes for username and remember-me
                .and()
                .rememberMe()
                    .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                    .key("smth")
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .logoutSuccessUrl("/login")
*/
        ;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(applicationUserServiceImpl);
        return daoAuthenticationProvider;
    }

//    @Bean
//    CommandLineRunner runner(UserRepository repo) {
//        return args -> {
//            List<ApplicationUser> users = List.of(
//                    new ApplicationUser(1, "linda", passwordEncoder.encode("pass"),  ApplicationUserRoles.ADMIN.getGrantedAuthorities()),
//                    new ApplicationUser(2, "anna",  passwordEncoder.encode("pass"), ApplicationUserRoles.STUDENT.getGrantedAuthorities()),
//                    new ApplicationUser(3, "tom", passwordEncoder.encode("pass"),  ApplicationUserRoles.ADMINTRAINEE.getGrantedAuthorities())
//            );
//            repo.insert(users);
//        };
//    }
}
