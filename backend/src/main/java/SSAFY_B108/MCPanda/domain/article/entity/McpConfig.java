package SSAFY_B108.MCPanda.domain.article.entity;

import java.util.Map;

public class McpConfig {
    private Map<String, McpProvider> mcpProviders;

    public McpConfig() {
    }

    public McpConfig(Map<String, McpProvider> mcpProviders) {
        this.mcpProviders = mcpProviders;
    }

    public Map<String, McpProvider> getMcpProviders() {
        return mcpProviders;
    }

    public void setMcpProviders(Map<String, McpProvider> mcpProviders) {
        this.mcpProviders = mcpProviders;
    }

    public static class McpProvider {
        private Map<String, McpServer> mcpServers;

        public McpProvider() {
        }

        public McpProvider(Map<String, McpServer> mcpServers) {
            this.mcpServers = mcpServers;
        }

        public Map<String, McpServer> getMcpServers() {
            return mcpServers;
        }

        public void setMcpServers(Map<String, McpServer> mcpServers) {
            this.mcpServers = mcpServers;
        }
    }

    public static class McpServer {
        private String command;
        private String[] args;
        private Map<String, String> env;
        private Boolean disabled;
        private String[] autoApprove;

        public McpServer() {
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String[] getArgs() {
            return args;
        }

        public void setArgs(String[] args) {
            this.args = args;
        }

        public Map<String, String> getEnv() {
            return env;
        }

        public void setEnv(Map<String, String> env) {
            this.env = env;
        }

        public Boolean getDisabled() {
            return disabled;
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
        }

        public String[] getAutoApprove() {
            return autoApprove;
        }

        public void setAutoApprove(String[] autoApprove) {
            this.autoApprove = autoApprove;
        }
    }
} 