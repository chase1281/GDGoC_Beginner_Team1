package com.example.StudyBoard.auth.entity;

import com.example.StudyBoard.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomMemberDetails implements UserDetails {
    private final Long memberId;
    private final String email;
    private final String name;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    @Builder
    public CustomMemberDetails(Long memberId, String email, String name, String password, Collection<? extends GrantedAuthority> authorities) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.authorities = authorities;
        this.password = password;
    }

    public static CustomMemberDetails from(Member member){
        return CustomMemberDetails.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .password(member.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name())))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() {return true;}

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isEnabled() {return true;}
}
