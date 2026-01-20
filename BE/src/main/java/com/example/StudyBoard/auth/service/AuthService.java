package com.example.StudyBoard.auth.service;

import com.example.StudyBoard.auth.dto.*;
import com.example.StudyBoard.auth.entity.RefreshToken;
import com.example.StudyBoard.auth.repository.TokenRepository;
import com.example.StudyBoard.member.entity.Member;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    @Transactional
    public LoginSuccessResponse login(@Validated final LoginRequest request){
        final Member member = getMemberByEmail(request.loginEmail());
        checkPassword(request, member);
        return generateAuthToken(member);
    }

    private Member getMemberByEmail(final String email) {
        final Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NULL_MEMBER));
        return member;
    }

    private Member getMemberById(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NULL_MEMBER));
        return member;
    }

    private void checkPassword(final LoginRequest request, final Member member){
        if(!passwordEncoder.matches(request.password(), member.getPassword())){
            throw new BusinessException(ErrorCode.INCORRECT_PASSWORD);
        }
    }

    private LoginSuccessResponse generateAuthToken(final Member member){
        String accessToken = jwtTokenProvider.generatedAccessToken(new MemberDetailRequest(member.getMemberId(), List.of(member.getRole())));
        String refreshToken = generateRefreshToken(member);
        String role = member.getRole().name();
        return new LoginSuccessResponse(accessToken, refreshToken, member.getMemberId().toString(), member.getName(), role);
    }

    private String generateRefreshToken(final Member member){
        RefreshToken token = new RefreshToken(member);

        if(tokenRepository.findByMember(member).isPresent()){
            tokenRepository.deleteAllByMember(member);
        }

        tokenRepository.save(token);
        String refreshToken = jwtTokenProvider.generatedRefreshToken(new MemberDetailRequest(member.getMemberId(), List.of(member.getRole())), token.getId());

        token.putRefreshToken(refreshToken);
        return refreshToken;
    }

    @Transactional
    public LoginSuccessResponse refreshAuthToken(@Validated final RefreshTokenRequest request){
        if(!jwtTokenProvider.isNotExpiredToken(request.refreshToken())){
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        Long memberId = jwtTokenProvider.getMemberFromToken(request.refreshToken());
        Member member = getMemberById(memberId);
        return generateAuthToken(member);
    }


    @Transactional
    public LoginSuccessResponse logout(@Validated final LoginMemberRequest loginMemberRequest){
        Member member = getMemberById(loginMemberRequest.memberId());
        tokenRepository.deleteAllByMember(member);
        return new LoginSuccessResponse(null, null, null, null, null);
    }
}
