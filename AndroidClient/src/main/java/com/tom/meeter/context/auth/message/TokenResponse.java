package com.tom.meeter.context.auth.message;

import java.util.Objects;

public final class TokenResponse {
    private String token;
    private String uuid;

    public TokenResponse() {
    }

    public TokenResponse(String token, String uuid) {
        this.token = token;
        this.uuid = uuid;
    }

    public String getToken() {
        return token;
    }

    public String getUuid() {
        return uuid;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TokenResponse) obj;
        return Objects.equals(this.token, that.token) &&
              Objects.equals(this.uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, uuid);
    }

    @Override
    public String toString() {
        return "TokenResponse[" +
              "token=" + token + ", " +
              "uuid=" + uuid + ']';
    }

}
