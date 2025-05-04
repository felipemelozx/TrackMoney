package fun.trackmoney.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Entity representing a user in the system.
 * A user has a unique identifier, name, email, and password.
 */
@Entity
@Table(name = "tb_user")
public class UserEntity {

  /**
   * Unique identifier for the user.
   * The UUID is automatically generated.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID userId;

  /**
   * The name of the user.
   */
  private String name;

  /**
   * The email address of the user.
   */
  private String email;

  /**
   * The password of the user.
   * It is stored in plain text (should be encrypted in production).
   */
  private String password;

  /**
   * Default constructor for the UserEntity class.
   */
  public UserEntity() {
  }

  /**
   * Constructor to initialize a user entity with the provided values.
   *
   * @param userId The unique identifier for the user.
   * @param name The name of the user.
   * @param email The email address of the user.
   * @param password The password of the user.
   */
  public UserEntity(UUID userId, String name, String email, String password) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.password = password;
  }

  /**
   * Gets the unique identifier for the user.
   *
   * @return The unique user ID.
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Sets the unique identifier for the user.
   *
   * @param userId The unique user ID to set.
   */
  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  /**
   * Gets the name of the user.
   *
   * @return The name of the user.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the user.
   *
   * @param name The name to set for the user.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the email address of the user.
   *
   * @return The email address of the user.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email address of the user.
   *
   * @param email The email address to set for the user.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the password of the user.
   *
   * @return The password of the user.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password of the user.
   *
   * @param password The password to set for the user.
   */
  public void setPassword(String password) {
    this.password = password;
  }
}
