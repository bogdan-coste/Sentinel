package com.sentinel.utils;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@Component
public class PublicIdGenerator {

    public String generatePublicId(){
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[8]);
        bb.putLong(uuid.getMostSignificantBits());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bb.array());
    }
}
