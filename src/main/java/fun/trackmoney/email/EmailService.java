package fun.trackmoney.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;


  public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
    this.javaMailSender = javaMailSender;
    this.templateEngine = templateEngine;
  }

  @Async
  public void sendEmailToVerifyEmail(String to, String name, Integer code) throws MessagingException {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

    messageHelper.setTo(to);
    messageHelper.setSubject("Confirm your e-mail");

    Context context = new Context();
    context.setVariable("verificationCode", code);
    context.setVariable("userName", name);

    String htmlContent = templateEngine.process("email-template-verify-email", context);
    messageHelper.setText(htmlContent, true);

    javaMailSender.send(mimeMessage);
  }
}
