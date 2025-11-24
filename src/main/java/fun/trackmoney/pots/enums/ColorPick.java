package fun.trackmoney.pots.enums;

import java.util.Arrays;

public enum ColorPick {

  DARK_BLUE(0, "#1E1E2E"),
  GRAPHITE_BLUE(1, "#2A2A40"),
  GRAYISH_BLUE(2, "#3A3A55"),
  SOFT_BLACK(3, "#242424"),
  SLATE_BLUE(4, "#334155");

  private final int code;
  private final String hex;

  ColorPick(int code, String hex) {
    this.code = code;
    this.hex = hex;
  }

  public int getCode() {
    return code;
  }

  public String getHex() {
    return hex;
  }

  public static ColorPick fromCode(int code) {
    return Arrays.stream(values())
        .filter(c -> c.code == code)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Código inválido para ColorPick: " + code));
  }
}
