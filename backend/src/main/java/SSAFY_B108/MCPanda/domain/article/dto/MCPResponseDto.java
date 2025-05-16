package SSAFY_B108.MCPanda.domain.article.dto;

import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "MCP 응답 DTO")
public class MCPResponseDto {
    
    @Schema(description = "MCP ID", example = "6142f4bed7ae97431faaa1d5")
    private String id;
    
    @Schema(description = "MCP 이름", example = "AWS")
    private String name;
    
    @Schema(description = "MCP 카테고리", example = "Backend")
    private String category;
    
    @Schema(description = "MCP 서버 구성 정보")
    private Map<String, Object> mcpServers;
    
    // 기본 생성자
    public MCPResponseDto() {
    }
    
    // 모든 필드를 받는 생성자
    public MCPResponseDto(String id, String name, String category, Map<String, Object> mcpServers) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.mcpServers = mcpServers;
    }
    
    // Getters & Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Map<String, Object> getMcpServers() {
        return mcpServers;
    }
    
    public void setMcpServers(Map<String, Object> mcpServers) {
        this.mcpServers = mcpServers;
    }
}
