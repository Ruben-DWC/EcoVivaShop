package com.ecovivashop.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class CustomOAuth2User implements OidcUser {

    private final OidcUser oidcUser;
    private final Integer userId;
    private final String role;

    public CustomOAuth2User(OidcUser oidcUser, Integer userId, String role) {
        this.oidcUser = oidcUser;
        this.userId = userId;
        this.role = role;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null && !role.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        // Agregar authorities del OAuth2 user original si existen
        if (oidcUser.getAuthorities() != null) {
            authorities.addAll(oidcUser.getAuthorities());
        }
        return authorities;
    }

    @Override
    public String getName() {
        return oidcUser.getAttribute("name");
    }

    @Override
    public String getEmail() {
        return oidcUser.getAttribute("email");
    }

    public String getFirstName() {
        return oidcUser.getAttribute("given_name");
    }

    public String getLastName() {
        return oidcUser.getAttribute("family_name");
    }

    @Override
    public String getPicture() {
        return oidcUser.getAttribute("picture");
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    public Integer getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}