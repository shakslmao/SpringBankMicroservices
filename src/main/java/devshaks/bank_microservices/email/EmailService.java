package devshaks.bank_microservices.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class responsible for sending emails.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    // Injected JavaMailSender for sending emails
    private final JavaMailSender mailSender;

    // Injected SpringTemplateEngine for processing email templates
    private final SpringTemplateEngine templateEngine;

    /**
     * Sends a validation email asynchronously.
     *
     * @param to              The recipient email address
     * @param username        The username to be included in the email
     * @param emailTemplate   The email template to be used
     * @param confirmationURL The confirmation URL to be included in the email
     * @param activationCode  The activation code to be included in the email
     * @param subject         The subject of the email
     * @throws MessagingException If there is an error creating or sending the email
     */
    @Async
    public void sendValidationEmail(String to, String username, EmailTemplateName emailTemplate, String confirmationURL, String activationCode, String subject) throws MessagingException {

        // Determine the email template name, defaulting to "confirm-email" if none is provided
        String emailTemplateName;
        if (emailTemplate == null) {
            emailTemplateName = "confirm-email";
        } else {
            emailTemplateName = emailTemplate.getName();
        }

        // Create a MIME email message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());

        // Prepare the email content properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationURL", confirmationURL);
        properties.put("activation_code", activationCode);

        // Create the Thymeleaf context and set the variables
        Context context = new Context();
        context.setVariables(properties);

        // Set email sender, recipient, and subject
        mimeMessageHelper.setFrom("contact@shaksdev.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);

        // Process the email template with the context
        String template = templateEngine.process(emailTemplateName, context);

        // Set the email body content and send the email
        mimeMessageHelper.setText(template, true);
        mailSender.send(mimeMessage);
    }
}
