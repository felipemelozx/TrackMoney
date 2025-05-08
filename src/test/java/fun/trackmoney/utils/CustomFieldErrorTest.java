package fun.trackmoney.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomFieldErrorTest {

  @Test
  void defaultConstructorShouldInitializeFieldsToNull() {
    CustomFieldError error = new CustomFieldError();
    assertNull(error.getField());
    assertNull(error.getMessage());
  }

  @Test
  void allArgsConstructorShouldSetFieldsCorrectly() {
    String fieldName = "testField";
    String errorMessage = "Test error message";
    CustomFieldError error = new CustomFieldError(fieldName, errorMessage);
    assertEquals(fieldName, error.getField());
    assertEquals(errorMessage, error.getMessage());
  }

  @Test
  void settersAndGettersShouldWorkCorrectly() {
    CustomFieldError error = new CustomFieldError();
    String newField = "anotherField";
    String newMessage = "Another message";

    error.setField(newField);
    error.setMessage(newMessage);

    assertEquals(newField, error.getField());
    assertEquals(newMessage, error.getMessage());
  }
}