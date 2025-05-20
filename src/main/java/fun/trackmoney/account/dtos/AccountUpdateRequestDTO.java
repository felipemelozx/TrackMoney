package fun.trackmoney.account.dtos;

public record AccountUpdateRequestDTO(
    String name,
    Boolean isAccountDefault
) {}
