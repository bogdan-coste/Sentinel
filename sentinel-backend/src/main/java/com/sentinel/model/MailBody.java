package com.sentinel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailBody {
    private String to;
    private String subject;
    private String content;
    private String templateName;
    private Map<String, Object> variables;
}
