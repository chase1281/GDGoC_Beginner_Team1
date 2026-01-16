package com.example.StudyBoard.auth.entity;

import com.example.StudyBoard.auth.service.JwtTokenProvider;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomMemberDetailService customMemberDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(!(authentication instanceof JwtAuthenticationToken)){
            return null;
        }
        String accessToken = authentication.getCredentials().toString();

        Claims claims = jwtTokenProvider.getClaimsByToken(accessToken);
        if(claims.get("auth") == null){
            throw new BusinessException(ErrorCode.TOKEN_MISSING_AUTHORITY);
        }

        Long memberId = claims.get("memberId", Long.class);

        CustomMemberDetails customMemberDetails = customMemberDetailService.loadMemberByMemberId(memberId);
        return new JwtAuthenticationToken(memberId, accessToken, customMemberDetails.getAuthorities());

    }

    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
