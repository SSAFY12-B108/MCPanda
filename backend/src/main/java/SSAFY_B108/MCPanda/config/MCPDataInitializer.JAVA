package SSAFY_B108.MCPanda.config;

import SSAFY_B108.MCPanda.domain.article.entity.MCP;
import SSAFY_B108.MCPanda.domain.article.repository.MCPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 애플리케이션 시작 시 MCP 데이터를 초기화하는 컴포넌트
 */
@Component
public class MCPDataInitializer implements CommandLineRunner {

    private final MCPRepository mcpRepository;

    @Autowired
    public MCPDataInitializer(MCPRepository mcpRepository) {
        this.mcpRepository = mcpRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // AWS MCP 데이터 초기화
        initAwsMcpData();
        // ETC MCP 데이터 초기화
        initEtcMcpData();
    }

    private void initAwsMcpData() {
        // AWS MCP가 이미 존재하는지 확인
        if (mcpRepository.findByName("AWS").isPresent()) {
            System.out.println("AWS MCP 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        // AWS 하위 MCP 서버 구성

        // 1. core-mcp-server 설정
        Map<String, Object> awsCore = new HashMap<>();
        awsCore.put("command", "uvx");
        awsCore.put("args", new String[]{"awslabs.core-mcp-server@latest"});
        Map<String, String> awsCoreEnv = new HashMap<>();
        awsCoreEnv.put("FASTMCP_LOG_LEVEL", "ERROR");
        awsCore.put("env", awsCoreEnv);

        // 2. cdk-mcp-server 설정
        Map<String, Object> awsCdk = new HashMap<>();
        awsCdk.put("command", "uvx");
        awsCdk.put("args", new String[]{"awslabs.cdk-mcp-server@latest"});
        Map<String, String> awsCdkEnv = new HashMap<>();
        awsCdkEnv.put("FASTMCP_LOG_LEVEL", "ERROR");
        awsCdk.put("env", awsCdkEnv);

        // 3. lambda-mcp-server 설정
        Map<String, Object> awsLambda = new HashMap<>();
        awsLambda.put("command", "uvx");
        awsLambda.put("args", new String[]{"awslabs.lambda-mcp-server@latest"});
        Map<String, Object> awsLambdaEnv = new HashMap<>();
        awsLambdaEnv.put("AWS_PROFILE", "your-aws-profile");
        awsLambdaEnv.put("AWS_REGION", "us-east-1");
        awsLambdaEnv.put("FUNCTION_PREFIX", "your-function-prefix");
        awsLambdaEnv.put("FUNCTION_LIST", "your-first-function, your-second-function");
        awsLambdaEnv.put("FUNCTION_TAG_KEY", "your-tag-key");
        awsLambdaEnv.put("FUNCTION_TAG_VALUE", "your-tag-value");
        awsLambda.put("env", awsLambdaEnv);

        // 4. cost-analysis-mcp-server 설정
        Map<String, Object> awsCost = new HashMap<>();
        awsCost.put("command", "uvx");
        awsCost.put("args", new String[]{"awslabs.cost-analysis-mcp-server@latest"});
        Map<String, String> awsCostEnv = new HashMap<>();
        awsCostEnv.put("AWS_PROFILE", "your-aws-profile");
        awsCostEnv.put("FASTMCP_LOG_LEVEL", "ERROR");
        awsCost.put("env", awsCostEnv);

        // MCP 서버 맵 구성
        Map<String, Object> mcpServers = new HashMap<>();
        mcpServers.put("awslabs.core-mcp-server", awsCore);
        mcpServers.put("awslabs.cdk-mcp-server", awsCdk);
        mcpServers.put("awslabs.lambda-mcp-server", awsLambda);
        mcpServers.put("awslabs.cost-analysis-mcp-server", awsCost);

        // AWS MCP 엔티티 생성 및 저장
        MCP awsMcp = new MCP("AWS", "Infrastructure", mcpServers);
        mcpRepository.save(awsMcp);
        
        System.out.println("AWS MCP 데이터가 초기화되었습니다.");
    }

    /**
     * ETC 카테고리의 MCP 데이터 초기화
     */
    private void initEtcMcpData() {
        // ETC MCP가 이미 존재하는지 확인
        if (mcpRepository.findByName("ETC").isPresent()) {
            System.out.println("ETC MCP 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        // ETC 하위 MCP 서버 구성
        // 1. misc-tools-mcp-server 설정
        Map<String, Object> miscTools = new HashMap<>();
        miscTools.put("command", "uvx");
        miscTools.put("args", new String[]{"othertools.misc-tools-mcp-server@latest"});
        Map<String, String> miscToolsEnv = new HashMap<>();
        miscToolsEnv.put("FASTMCP_LOG_LEVEL", "ERROR");
        miscTools.put("env", miscToolsEnv);

        // 2. monitoring-mcp-server 설정
        Map<String, Object> monitoring = new HashMap<>();
        monitoring.put("command", "uvx");
        monitoring.put("args", new String[]{"othertools.monitoring-mcp-server@latest"});
        Map<String, String> monitoringEnv = new HashMap<>();
        monitoringEnv.put("FASTMCP_LOG_LEVEL", "ERROR");
        monitoring.put("env", monitoringEnv);

        // 3. analytics-mcp-server 설정
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("command", "uvx");
        analytics.put("args", new String[]{"othertools.analytics-mcp-server@latest"});
        Map<String, Object> analyticsEnv = new HashMap<>();
        analyticsEnv.put("DATA_SOURCE", "your-data-source");
        analyticsEnv.put("OUTPUT_FORMAT", "json");
        analytics.put("env", analyticsEnv);

        // MCP 서버 맵 구성
        Map<String, Object> etcMcpServers = new HashMap<>();
        etcMcpServers.put("othertools.misc-tools-mcp-server", miscTools);
        etcMcpServers.put("othertools.monitoring-mcp-server", monitoring);
        etcMcpServers.put("othertools.analytics-mcp-server", analytics);

        // ETC MCP 엔티티 생성 및 저장
        MCP etcMcp = new MCP("ETC", "ETC", etcMcpServers);
        mcpRepository.save(etcMcp);
        
        System.out.println("ETC MCP 데이터가 초기화되었습니다.");
    }
}