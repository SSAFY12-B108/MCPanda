package SSAFY_B108.MCPanda.domain.article.dto;

import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "MCP 등록 요청 DTO")
public class MCPCreateRequestDto {
    
    @Schema(description = "MCP 이름 (AWS, GCP 등)", example = "AWS", required = true)
    private String name;
    
    @Schema(description = "MCP 서버 구성 정보", required = true)
    private Map<String, Object> mcpServers;
    
    // 기본 생성자
    public MCPCreateRequestDto() {
    }
    
    // 모든 필드를 받는 생성자
    public MCPCreateRequestDto(String name, Map<String, Object> mcpServers) {
        this.name = name;
        this.mcpServers = mcpServers;
    }
    
    // Getters & Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Map<String, Object> getMcpServers() {
        return mcpServers;
    }
    
    public void setMcpServers(Map<String, Object> mcpServers) {
        this.mcpServers = mcpServers;
    }
}
