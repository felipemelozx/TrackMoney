package fun.trackmoney.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

  private JavaMailSender mailSender;
  private TemplateEngine templateEngine;
  private EmailService emailService;

  @BeforeEach
  void setUp() {
    mailSender = mock(JavaMailSender.class);
    templateEngine = mock(TemplateEngine.class);
    emailService = new EmailService(mailSender, templateEngine);
  }

  @Test
  void testSendEmailSuccess() throws MessagingException {
    String to = "test@example.com";
    String name = "Test User";
    Integer code = 1234;
    MimeMessage mimeMessage = mock(MimeMessage.class);

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("email-template-verify-email"), any(Context.class)))
        .thenReturn("<html>Email Content</html>");

    emailService.sendEmailToVerifyEmail(to, name, code);

    verify(mailSender).createMimeMessage();
    verify(templateEngine).process(eq("email-template-verify-email"), any(Context.class));
    verify(mailSender).send(mimeMessage);
  }

  @Test
  void testSendEmailThrowsMessagingException() {
    String to = "test@example.com";
    String name = "Test User";
    Integer code = 1234;
    when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail error"));

    assertThrows(RuntimeException.class, () -> emailService.sendEmailToVerifyEmail(to, name, code));
  }
}