package com.mahivkhanwasi.auth_service.dto;

import java.util.List;

public record RequestUserDTO(
        String username,
        String password,
        String firstName,
        String lastName,
        List<String> roles
) {
}
