package com.leedae.boardandadmin.dto.security;

import java.util.Map;

public record NaverOAuth2Response(
        String resultcode,
        String message,
        Response response
) {
    public record Response(
            String id,
            String email,
            String name,
            String nickname
    ) {
        public static Response from(Map<String, Object> attributes) {
            return new Response(
                    String.valueOf(attributes.get("id")),
                    String.valueOf(attributes.get("email")),
                    String.valueOf(attributes.get("name")),
                    String.valueOf(attributes.get("nickname"))
            );
        }
    }

    public static NaverOAuth2Response from(Map<String, Object> attributes) {
        return new NaverOAuth2Response(
                String.valueOf(attributes.get("resultcode")),
                String.valueOf(attributes.get("message")),
                Response.from((Map<String, Object>) attributes.get("response"))
        );
    }

    public String email() { return this.response().email(); }
    public String nickname() { return this.response().nickname(); }
}