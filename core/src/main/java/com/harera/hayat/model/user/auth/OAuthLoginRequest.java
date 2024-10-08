package com.harera.hayat.model.user.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OAuthLoginRequest {

    @JsonProperty("firebase_token")
    private String firebaseToken;

    @JsonProperty("device_token")
    private String deviceToken;
}
