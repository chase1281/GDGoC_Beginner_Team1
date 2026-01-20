package com.example.StudyBoard.auth.entity;

import com.example.StudyBoard.member.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tokenId")
    private Long id;

    @Column
    private String refreshToken;

    @Column
    private boolean isExpired = false;

    @Column
    private LocalDateTime recentLogin = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    protected RefreshToken() {
    }

    public RefreshToken(Member member){
        this.member = member;
    }

    public Long getId(){
        return id;
    }

    public void putRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public void expire(){
        isExpired = true;
    }

    public boolean isExpired(){
        return isExpired;
    }
}
