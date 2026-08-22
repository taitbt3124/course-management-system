package com.cms.security.jwt;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // Bộ nhớ RAM lưu trữ token và thời gian hết hạn
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    // Thêm Token vào Blacklist
    public void blacklistToken(String token, long expirationTimeInMs) {
        if (expirationTimeInMs > 0) {
            blacklist.put(token, System.currentTimeMillis() + expirationTimeInMs);
        }
    }

    // Kiểm tra Token đã bị thu hồi chưa
    public boolean isBlacklisted(String token) {
        Long expiryTime = blacklist.get(token);
        if (expiryTime == null) {
            return false;
        }

        // Tự động giải phóng bộ nhớ nếu token đã hết hạn hoàn toàn
        if (System.currentTimeMillis() > expiryTime) {
            blacklist.remove(token);
            return false;
        }

        return true;
    }
}