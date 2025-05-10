package org.gamja.gamzatechblog.core.auth.oauth.model;

// after
public interface OAuthUserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
    String getProfileImageUrl();  // 이름 변경
}


