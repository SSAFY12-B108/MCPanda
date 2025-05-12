package SSAFY_B108.MCPanda.domain.article.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "mcps")
public class MCP {

    @Id
    private String id;
    
    private String name; // MCP 이름 (예: "AWS")
    
    private Map<String, Object> mcpServers; // MCP 서버 정보
    
    public MCP() {
    }
    
    public MCP(String name, Map<String, Object> mcpServers) {
        this.name = name;
        this.mcpServers = mcpServers;
    }
    
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
    
    public Map<String, Object> getMcpServers() {
        return mcpServers;
    }
    
    public void setMcpServers(Map<String, Object> mcpServers) {
        this.mcpServers = mcpServers;
    }
}
