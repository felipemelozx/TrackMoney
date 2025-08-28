package fun.trackmoney.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
          return false;
        }

        StringBuilder errorMessage = new StringBuilder();

        if (!PasswordCheck.LOWERCASE_PATTERN.matcher(password).find()) {
            errorMessage.append(" Must contain lowercase.");
        }
        if (!PasswordCheck.UPPERCASE_PATTERN.matcher(password).find()) {
            errorMessage.append(" Must contain uppercase.");
        }
        if (!PasswordCheck.DIGIT_PATTERN.matcher(password).find()) {
            errorMessage.append(" Must contain a digit.");
        }
        if (!PasswordCheck.SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errorMessage.append(" Must contain a special character.");
        }
        if (!PasswordCheck.LENGTH_PATTERN.matcher(password).matches()) {
            errorMessage.append(" Must be at least 8 characters long.");
        }

        if (errorMessage.length() > 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage.toString().trim())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
