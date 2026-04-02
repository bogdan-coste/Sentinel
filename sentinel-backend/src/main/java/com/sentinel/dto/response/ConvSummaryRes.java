package com.sentinel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvSummaryRes {

    private UserRes userEntity;
    private MessageRes lastMessage;
    private long unreadCount;

}
