package SSAFY_B108.MCPanda.domain.member.service;

import SSAFY_B108.MCPanda.domain.member.entity.Member;
import SSAFY_B108.MCPanda.domain.member.repository.MemberRepository;
import SSAFY_B108.MCPanda.domain.auth.oauth2.dto.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 *  회원 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * OAuth2 사용자 정보로 회원 저장 또는 업데이트
     * 1. 제공자+제공자ID로 회원 조회
     * 2. 있으면 정보 업데이트
     * 3. 없으면 이메일로 조회 (다른 제공자로 가입 여부 확인)
     * 4. 이메일로도 없으면 새 회원 생성
     */
    public Member saveOrUpdateOAuth2User(OAuth2UserInfo userInfo, String registrationId) {
        log.info("OAuth2 사용자 정보 처리: email={}, provider={}", userInfo.getEmail(), registrationId);

        // email로 회원 조회
        Optional<Member> memberByEmail = memberRepository.findByEmail(userInfo.getEmail());

        if(memberByEmail.isPresent()) {
            // 일치하는 회원이 있는 경우
            Member existringMember = memberByEmail.get();

            // 이미 해당 제공자로 연동된 계정인지 확인
            if (existringMember.getOauthIds() != null && existringMember.getOauthIds().containsKey(registrationId)) {
                // 기존 제공자로 로그인한 경우, 정보만 업데이트
                log.info("기존 회원 정보 업데이트: email={}, provider={}", userInfo.getEmail(), registrationId);
                return updateExistingMember(existringMember,userInfo);
            } else {
                // 다른 제공자로 로그인했지만 동일 이메일 -> 계정 연동
                log.info("계정 연동: email={}, provider={}", userInfo.getEmail(), registrationId);
                existringMember.addOAuthId(registrationId,userInfo.getId());
                return updateExistingMember(existringMember,userInfo);
            }
        } else {
            // 새 회원 생성
            log.info("새 회원 생성: email={}, provider={}",userInfo.getEmail(), registrationId);
            return createNewMember(userInfo,registrationId);
        }
    }

    /**
     *  기존 회원 정보 업데이트
     * @param member
     * @param userInfo
     * @return
     */
    private Member updateExistingMember(Member member, OAuth2UserInfo userInfo) {
        member.update(userInfo.getName(), userInfo.getImageUrl());
        return memberRepository.save(member);
    }
    /**
     * 새 회원 생성
     * @param userInfo
     * @param registrationId
     * @return
     */
    private Member createNewMember(OAuth2UserInfo userInfo, String registrationId) {
        // 닉네임 생성
        String nickname = generateNickname(userInfo.getEmail());
        
        Member newMember = Member.builder()
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .nickname(nickname)
                .profileImage(userInfo.getImageUrl())
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // OAuth 제공자 정보 추가
        newMember.addOAuthId(registrationId,userInfo.getId());

        return memberRepository.save(newMember);
    }

    /**
     * 고유한 닉네임 생성 (이메일#랜덤4자리)
     * @param email
     * @return
     */
    private String generateNickname(String email) {
        String username;
        try {
            username = email.split("@")[0];
        } catch (Exception e) {
            username = email;
        }

        StringBuilder nickname;
        boolean isUnique = false;
        int attempts = 0;

        do {
            // 랜덤 4자리 숫자 생성
            int randomNum = (int) (Math.random() * 9000) + 1000;
            nickname = new StringBuilder(username).append("#").append(randomNum);

            // 닉네임 중복 확인
            long count = memberRepository.countByNickname(nickname.toString());
            isUnique = (count == 0);

            attempts++;
        } while (!isUnique && attempts < 5); // 최대 5번 시도

        if (!isUnique) {
            // 5번 시도 후에도 중복이면 타임스탬프 추가
            long timestamp = System.currentTimeMillis() % 10000;
            nickname = new StringBuilder(username).append("#").append(timestamp);
        }

        return nickname.toString();
    }

    /**
     * 회원 탈퇴 처리
     * - 개인정보 익명화
     * - 계정 비활성화 (soft delete)
     * - OAuth 연동 정보 제거
     *
     * @param memberId 탈퇴할 회원 ID
     * @return 처리 결과 (성공 여부)
     */
    public boolean withdrawMember(ObjectId memberId) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);

        if (memberOpt.isEmpty()) {
            log.warn("탈퇴 처리 실패: 존재하지 않는 회원 ID - {}", memberId);
            return false;
        }

        Member member = memberOpt.get();

        // 이미 탈퇴한 회원인지 확인
        if (member.getDeletedAt() != null) {
            log.warn("이미 탈퇴한 회원입니다: {}", memberId);
            return false;
        }

        try {
            // 1. 개인정보 익명화
            anonymizePersonalData(member);

            // 2. 계정 비활성화 (soft delete)
            member.delete(); // Member 엔티티의 delete() 메서드 호출 (deletedAt 설정)

            // 3. 회원 정보 저장
            memberRepository.save(member);

            log.info("회원 탈퇴 처리 완료: {}", memberId);
            return true;
        } catch (Exception e) {
            log.error("회원 탈퇴 처리 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 회원 개인정보 익명화 처리
     * @param member 익명화할 회원
     */
    private void anonymizePersonalData(Member member) {
        // 고유 식별자 생성 (UUID 뒷부분 8자리)
        String anonymousId = UUID.randomUUID().toString().substring(24);

        // 이메일 익명화 (이메일 형식 유지하면서 식별 불가능하게)
        member.anonymizeEmail("withdrawn" + anonymousId + "@anonymous.com");

        // 이름 익명화
        member.anonymizeName("탈퇴회원");

        // 닉네임 익명화 (중복 방지를 위해 고유 ID 포함)
        member.updateNickname("탈퇴회원#" + anonymousId);

        // 프로필 이미지 제거
        member.removeProfileImage();

        // OAuth 연동 정보 제거
        member.clearOAuthIds();
    }
}
