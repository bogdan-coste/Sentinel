package com.sentinel.model;

import lombok.Data;

@Data
public class CommentRequestDto {

    private Long mediaId;
    private String content;
}
