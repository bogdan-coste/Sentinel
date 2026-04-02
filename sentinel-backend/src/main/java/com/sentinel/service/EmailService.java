package com.sentinel.service;

import com.sentinel.entity.MailEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine){
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Transactional(rollbackFor = Exception.class)
    @Async
    public void sendVerificationEmail(MailEntity mailEntity) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(mailEntity.getTo());
            helper.setSubject(mailEntity.getSubject());
            helper.setFrom("noreply@sentinel-app.com");

            String htmlContent;
            if (mailEntity.getTemplateName() != null && mailEntity.getVariables() != null) {
                Context context = new Context();
                context.setVariables(mailEntity.getVariables());
                htmlContent = templateEngine.process(mailEntity.getTemplateName(), context);
            } else {
                htmlContent = mailEntity.getContent();
            }

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Email has been sent successfully to {}", mailEntity.getTo());
        }catch(MessagingException e){
            log.error("Error sending verification mail to {}", mailEntity.getTo(), e);
        }

    }
}
