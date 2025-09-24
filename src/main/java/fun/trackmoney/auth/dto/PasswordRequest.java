package fun.trackmoney.auth.dto;

import fun.trackmoney.utils.ValidPassword;

public record PasswordRequest(@ValidPassword String newPassword) {
}
