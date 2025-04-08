package com.mahivkhanwasi.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisUserSession implements Serializable {
    private String fullName;
    private String username;
    private String token;
}
