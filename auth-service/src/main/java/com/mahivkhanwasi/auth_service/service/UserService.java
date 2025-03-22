package com.mahivkhanwasi.auth_service.service;

import com.mahivkhanwasi.auth_service.dto.RequestLoginDTO;
import com.mahivkhanwasi.auth_service.dto.RequestUserDTO;

import java.util.Map;

public interface UserService {

    public String register(RequestUserDTO dto);
    public Map<String, Object> login(RequestLoginDTO dto);
}
