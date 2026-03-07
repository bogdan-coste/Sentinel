package com.sentinel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailEntity {
    private String to;
    private String subject;
    private String content;
    private String templateName;
    private Map<String, Object> variables;
}
