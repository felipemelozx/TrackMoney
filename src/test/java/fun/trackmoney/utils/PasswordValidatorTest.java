package fun.trackmoney.utils;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordValidatorTest {

    private PasswordValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void shouldReturnFalse_whenPasswordIsNull() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void shouldReturnFalse_whenPasswordIsMissingLowercase() {
        String password = "PASSWORD1#";
        assertFalse(validator.isValid(password, context));
    }

    @Test
    void shouldReturnFalse_whenPasswordIsMissingUppercase() {
        String password = "password1#";
        assertFalse(validator.isValid(password, context));
    }

    @Test
    void shouldReturnFalse_whenPasswordIsMissingDigit() {
        String password = "Password#";
        assertFalse(validator.isValid(password, context));
    }

    @Test
    void shouldReturnFalse_whenPasswordIsMissingSpecialCharacter() {
        String password = "Password1";
        assertFalse(validator.isValid(password, context));
    }

    @Test
    void shouldReturnFalse_whenPasswordIsTooShort() {
        String password = "Pa1#";
        assertFalse(validator.isValid(password, context));
    }

    @Test
    void shouldReturnTrue_whenPasswordIsValid() {
        String password = "Password1#";
        assertTrue(validator.isValid(password, context));
    }
}
