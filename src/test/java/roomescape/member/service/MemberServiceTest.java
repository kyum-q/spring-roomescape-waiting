package roomescape.member.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.exception.BadRequestException;
import roomescape.member.repository.MemberRepository;
import roomescape.member.domain.Member;
import roomescape.member.dto.MemberLoginRequest;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    private static final String USERNAME = "username";
    private static final String USER_EXAMPLE_COM = "user@example.com";
    private static final String USER_EXAMPLE_PASSWORD = "password";
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("등록된 이메일로 회원 정보를 정상적으로 조회한다")
    void findMember_WhenEmailIsRegistered() {
        // Given
        MemberLoginRequest request = new MemberLoginRequest(USER_EXAMPLE_COM, USER_EXAMPLE_PASSWORD);
        Member registeredMember = new Member(USERNAME, USER_EXAMPLE_COM, USER_EXAMPLE_PASSWORD);

        // When
        when(memberRepository.findByEmail(USER_EXAMPLE_COM))
                .thenReturn(Optional.of(registeredMember));

        // Then
        Member actualInfo = memberService.findMember(request);
        assertAll(() -> {
            assertEquals(registeredMember.getName(), actualInfo.getName());

        });
    }

    @Test
    @DisplayName("등록되지 않은 이메일로 조회 시 BadRequestException을 발생시킨다")
    void findMember_ShouldThrowException_WhenEmailIsNotRegistered() {
        // Given
        String email = "nonexistent@example.com";
        MemberLoginRequest request = new MemberLoginRequest(email, USER_EXAMPLE_PASSWORD);

        // When
        when(memberRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        // Then
        assertThrows(BadRequestException.class, () -> {
            memberService.findMember(request);
        });
    }

}
