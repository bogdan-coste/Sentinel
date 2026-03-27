package com.sentinel.service;

import com.sentinel.model.MailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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

    @Async
    public void sendVerificationEmail(MailBody mailBody) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(mailBody.getTo());
            helper.setSubject(mailBody.getSubject());
            helper.setFrom("noreply@sentinel-app.com");

            String htmlContent;
            if (mailBody.getTemplateName() != null && mailBody.getVariables() != null) {
                Context context = new Context();
                context.setVariables(mailBody.getVariables());
                htmlContent = templateEngine.process(mailBody.getTemplateName(), context);
            } else {
                htmlContent = mailBody.getContent();
            }

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Email has been sent successfully to {}", mailBody.getTo());
        }catch(MessagingException e){
            log.error("Error sending verification mail to {}", mailBody.getTo(), e);
        }

    }
}
