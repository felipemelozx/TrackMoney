package fun.trackmoney.pots.service;

import fun.trackmoney.account.exception.AccountNotFoundException;
import fun.trackmoney.account.repository.AccountRepository;
import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.mapper.PotsMapper;
import fun.trackmoney.pots.repository.PotsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PotsService {

  private final PotsRepository potsRepository;
  private final PotsMapper potsMapper;
  private final AccountRepository accountRepository;

  public PotsService(PotsRepository potsRepository, PotsMapper potsMapper, AccountRepository accountRepository) {
    this.potsRepository = potsRepository;
    this.potsMapper = potsMapper;
    this.accountRepository = accountRepository;
  }

  public List<PotsResponseDTO> findAllPots(Integer accountId) {
    return potsMapper.listToResponse(potsRepository.findAllPotsByAccountId(accountId));
  }

  public PotsResponseDTO create(CreatePotsDTO dto) {
    var entity = potsMapper.toEntity(dto);
    var account = accountRepository.findById(dto.accountId())
        .orElseThrow(() -> new AccountNotFoundException("Account not found exception for id: " + dto.accountId()));
    entity.setAccount(account);
    return potsMapper.toResponse(potsRepository.save(entity));
  }
}
