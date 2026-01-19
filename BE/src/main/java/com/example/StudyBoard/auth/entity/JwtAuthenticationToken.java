package com.example.StudyBoard.auth.entity;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private Long memberId;
    private String token;

    public JwtAuthenticationToken(String token){
        super(null);
        this.memberId = null;
        this.token = token;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(Long memberId, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.memberId = memberId;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return this.memberId;
    }

/*    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException{
        if(authenticated){
            throw new IllegalArgumentException("옳지 않은 과정을 통해 인증되었습니다.");
        }
        super.setAuthenticated(false);
    }*/

    @Override
    public void eraseCredentials(){
        super.eraseCredentials();
        this.token = null;
    }
}
