package com.sentinel.dto.request;

import lombok.Data;

@Data
public class CommentReq {

    private Long mediaId;
    private String content;
}
