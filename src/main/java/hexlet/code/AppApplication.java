package hexlet.code;

import com.rollbar.notifier.Rollbar;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.rollbar.spring.webmvc.RollbarSpringConfigBuilder.withAccessToken;


@SpringBootApplication
// Добавляем поддержку авторизации через токен в свагер
@SecurityScheme(name = "javainuseapi", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class AppApplication {

    // для тестирования Rollbag
    private static Rollbar rollbar = Rollbar.init(withAccessToken("21359dae98f14f8792c40d613e7cd463")
            .environment("qa")
            .codeVersion("1.0.0")
            .build());


    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
        rollbar.debug("Hello, Rollbar");
    }




    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void rollbarStart() throws Exception {
        Rollbar rollbar = Rollbar.init(withAccessToken("3c8203dd21094db9ba73ed32966fda3c")
                .environment("qa")
                .codeVersion("1.0.0")
                .build());

        rollbar.log("Hello, Rollbar");
        rollbar.close(true);
    }

}

