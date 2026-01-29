package com.example.StudyBoard.auth.service;

import com.example.StudyBoard.auth.dto.MemberDetailRequest;
import com.example.StudyBoard.exception.BusinessException;
import com.example.StudyBoard.exception.ErrorCode;
import com.example.StudyBoard.global.config.properites.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;

    //Access Token 생성
    public String generatedAccessToken(final MemberDetailRequest member){
        Claims claims = getClaimsFrom(member);
        return getTokenFrom(claims, jwtProperties.getAccessTokenValidTime() * 1000);
    }

    //Access Token Claim
    private Claims getClaimsFrom(final MemberDetailRequest member) {
        Claims claims = Jwts.claims();
        claims.put("memberId", member.memberId());
        claims.put("auth", member.authorities());
        return claims;
    }

    //Refresh Token 생성
    public String generatedRefreshToken(final MemberDetailRequest member, final Long tokenId){
        Claims claims = getClaimsFrom(member, tokenId);
        return getTokenFrom(claims, jwtProperties.getRefreshTokenValidTime() * 1000);
    }

    //Refresh Token Claim
    private Claims getClaimsFrom(final MemberDetailRequest member, final Long tokenId) {
        Claims claims = Jwts.claims();
        claims.put("memberId", member.memberId());
        claims.put("tokenId", tokenId);
        claims.put("auth", member.authorities());
        return claims;
    }

    //Claim 정보에서 Token 추출
    private String getTokenFrom(final Claims claims, final long validTime) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validTime))
                .signWith(
                        Keys.hmacShaKeyFor(jwtProperties.getBytesSecretKey()),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    //Token에서 회원 정보 추출
    public Long getMemberFromToken(final String token){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getBytesSecretKey()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("memberId", Long.class);
        }catch (ExpiredJwtException e){
            throw new BusinessException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    //AccessToken 값만 남도록 접두사 삭제
    public String extractAccessToken(final HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")){
            return token.substring(7);
        }
        return token;
    }

    //만료된 토큰 확인
    public boolean isNotExpiredToken(final String token){
        try{
            return !Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getBytesSecretKey()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration().before(new Date());
        }catch (ExpiredJwtException e){
            return false;
        }
    }

    //Token에서 Token Id 얻기
    public Long getTokenIdByToken(final String refreshToken){
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getBytesSecretKey()))
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            return Long.parseLong(String.valueOf(claims.get("tokenId")));
        }catch (ExpiredJwtException e){
            throw new BusinessException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    //Token에서 Claims 얻기
    public Claims getClaimsByToken(final String accessToken){
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getBytesSecretKey()))
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }
}
