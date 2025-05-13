package SSAFY_B108.MCPanda.domain.article.service;

import SSAFY_B108.MCPanda.domain.article.entity.MCP;
import SSAFY_B108.MCPanda.domain.article.repository.MCPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import SSAFY_B108.MCPanda.domain.article.dto.MCPCreateRequestDto;
import SSAFY_B108.MCPanda.domain.article.dto.MCPResponseDto;

@Service
public class MCPService {

    private final MCPRepository mcpRepository;

    @Autowired
    public MCPService(MCPRepository mcpRepository) {
        this.mcpRepository = mcpRepository;
    }

    /**
     * 새로운 MCP를 등록합니다.
     * @param mcp 등록할 MCP 정보
     * @return 등록된 MCP 정보
     */
    @Transactional
    public MCP registerMCP(MCP mcp) {
        // 이미 동일한 이름의 MCP가 존재하는지 확인
        Optional<MCP> existingMCP = mcpRepository.findByName(mcp.getName());
        if (existingMCP.isPresent()) {
            // 기존 MCP 정보 업데이트
            MCP updateMCP = existingMCP.get();
            updateMCP.setMcpServers(mcp.getMcpServers());
            return mcpRepository.save(updateMCP);
        }
        // 새 MCP 등록
        return mcpRepository.save(mcp);
    }

    /**
     * 모든 MCP 정보를 조회합니다.
     * @return MCP 목록
     */
    @Transactional(readOnly = true)
    public List<MCP> getAllMCPs() {
        return mcpRepository.findAll();
    }

    /**
     * 이름으로 특정 MCP 정보를 조회합니다.
     * @param name MCP 이름
     * @return 찾은 MCP 정보(Optional)
     */
    @Transactional(readOnly = true)
    public Optional<MCP> getMCPByName(String name) {
        return mcpRepository.findByName(name);
    }

    /**
     * ID로 특정 MCP 정보를 조회합니다.
     * @param id MCP ID
     * @return 찾은 MCP 정보(Optional)
     */
    @Transactional(readOnly = true)
    public Optional<MCP> getMCPById(String id) {
        return mcpRepository.findById(id);
    }

    /**
     * MCP 정보를 삭제합니다.
     * @param id 삭제할 MCP ID
     */
    @Transactional
    public void deleteMCP(String id) {
        mcpRepository.deleteById(id);
    }

    /**
     * DTO를 MCP 엔티티로 변환합니다.
     */
    public MCP convertToEntity(MCPCreateRequestDto dto) {
        return new MCP(dto.getName(), dto.getCategory(), dto.getMcpServers());
    }

    /**
     * MCP 엔티티를 ResponseDTO로 변환합니다.
     */
    public MCPResponseDto convertToDto(MCP mcp) {
        return new MCPResponseDto(mcp.getId(), mcp.getName(), mcp.getCategory(), mcp.getMcpServers());
    }

    /**
     * MCP 목록을 ResponseDTO 목록으로 변환합니다.
     */
    public List<MCPResponseDto> convertToDtoList(List<MCP> mcps) {
        return mcps.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
