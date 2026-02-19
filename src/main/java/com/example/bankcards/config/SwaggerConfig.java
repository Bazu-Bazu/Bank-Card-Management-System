package com.example.bankcards.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Card Management System API")
                        .version("1.0.0")
                        .description(
                                "При первом запуске приложения автоматически создается пользователь с ролью ADMIN. " +
                                "Его логин и пароль: 'admin'. " +
                                "Для работы с системой необходимо авторизоваться с этими данными и получить access-токен. " +
                                "Access-токен действителен в течение 20 минут. После его истечения необходимо обновить токен с помощью refresh-токена. " +
                                "Не забудьте указывать полученный токен в заголовке Authorization при обращении к защищённым эндпоинтам."
                        ));
    }

}
