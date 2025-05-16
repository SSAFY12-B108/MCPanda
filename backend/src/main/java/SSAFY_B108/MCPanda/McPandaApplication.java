package SSAFY_B108.MCPanda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "MCPanda API",
        version = "1.0.0",
        description = "MCPanda 프로젝트의 API 문서"
    )
)
public class McPandaApplication {
	public static void main(String[] args) {
		SpringApplication.run(McPandaApplication.class, args);
	}

}
