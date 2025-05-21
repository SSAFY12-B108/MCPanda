import { useQuery } from "@tanstack/react-query";
import apiClient from '@/api/client';

export interface Mcp {
  id: string;
  name: string;
  category: string;
  mcpServers: Record<string, string>;
}

export interface McpCategory {
  category: string;
  mcps: Mcp[];
}

const fetchMcps = async (): Promise<McpCategory[]> => {
  const response = await apiClient.get("/mcps");
  const data: Mcp[] = response.data;

  // Group MCPs by category
  const categorizedMcps: Record<string, Mcp[]> = data.reduce((acc, mcp) => {
    if (!acc[mcp.category]) {
      acc[mcp.category] = [];
    }
    acc[mcp.category].push(mcp);
    return acc;
  }, {} as Record<string, Mcp[]>);

  // Convert to array of { category, mcps } objects
  const result: McpCategory[] = Object.keys(categorizedMcps).map(category => ({
    category,
    mcps: categorizedMcps[category]
  }));

  return result;
};

const useMcps = () => {
  return useQuery({
    queryKey: ['mcps'],
    queryFn: fetchMcps,
  });
};

export default useMcps;
