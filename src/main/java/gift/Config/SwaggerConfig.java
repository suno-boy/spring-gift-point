package gift.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // OpenAPI 객체를 통해 API 문서의 기본 정보를 설정할 수 있다.
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("kakao tech camp step2")
                        .version("1.0")
                        .description("kakao tech camp step2 선물하기 기능 구현. of 순호")
                );
    }
}