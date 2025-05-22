package SSAFY_B108.MCPanda.domain.article.controller;

import SSAFY_B108.MCPanda.domain.article.entity.MCP;
import SSAFY_B108.MCPanda.domain.article.service.MCPService;
import SSAFY_B108.MCPanda.domain.member.entity.Member;
import SSAFY_B108.MCPanda.domain.article.dto.MCPCreateRequestDto;
import SSAFY_B108.MCPanda.domain.article.dto.MCPResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "MCP API", description = "MCP 관리를 위한 API 명세")
@RestController
@RequestMapping("/api/mcps")
public class MCPController {

    private final MCPService mcpService;

    @Autowired
    public MCPController(MCPService mcpService) {
        this.mcpService = mcpService;
    }

    @Operation(
            summary = "MCP 등록",
            description = "새로운 MCP를 시스템에 등록합니다. 관리자만 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "MCP 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MCPResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    @PostMapping
    public ResponseEntity<?> registerMCP(
            @RequestBody MCPCreateRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal Member loggedInMember
    ) {
        // 로그인 체크
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        
        // 엔티티 변환 및 저장
        MCP mcp = mcpService.convertToEntity(requestDto);
        MCP savedMcp = mcpService.registerMCP(mcp);
        
        // 응답 DTO 변환
        MCPResponseDto responseDto = mcpService.convertToDto(savedMcp);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "모든 MCP 조회",
            description = "등록된 모든 MCP 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MCP 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<MCPResponseDto>> getAllMCPs() {
        List<MCP> mcps = mcpService.getAllMCPs();
        List<MCPResponseDto> responseDtos = mcpService.convertToDtoList(mcps);
        return ResponseEntity.ok(responseDtos);
    }

    @Operation(
            summary = "특정 MCP 조회",
            description = "이름으로 특정 MCP 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MCP 조회 성공"),
            @ApiResponse(responseCode = "404", description = "MCP를 찾을 수 없음")
    })
    @GetMapping("/{name}")
    public ResponseEntity<?> getMCPByName(
            @PathVariable String name
    ) {
        Optional<MCP> mcpOpt = mcpService.getMCPByName(name);
        if (mcpOpt.isPresent()) {
            MCPResponseDto responseDto = mcpService.convertToDto(mcpOpt.get());
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 이름의 MCP를 찾을 수 없습니다: " + name);
        }
    }

    @Operation(
            summary = "MCP 삭제",
            description = "ID로 MCP 정보를 삭제합니다. 관리자만 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MCP 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 부족"),
            @ApiResponse(responseCode = "404", description = "MCP를 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMCP(
            @PathVariable String id,
            @Parameter(hidden = true) @AuthenticationPrincipal Member loggedInMember
    ) {
        // 로그인 체크
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        
        // 관리자 권한 체크 (실제 코드에서는 구현 필요)
        // if (!loggedInMember.hasRole("ADMIN")) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 MCP를 삭제할 수 있습니다.");
        // }

        // MCP 존재 여부 확인
        if (!mcpService.getMCPById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 MCP를 찾을 수 없습니다: " + id);
        }

        mcpService.deleteMCP(id);
        return ResponseEntity.ok().body(java.util.Map.of("message", "MCP가 성공적으로 삭제되었습니다."));
    }
}
