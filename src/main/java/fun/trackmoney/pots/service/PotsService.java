package fun.trackmoney.pots.service;

import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.mapper.PotsMapper;
import fun.trackmoney.pots.repository.PotsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PotsService {

  private final PotsRepository potsRepository;
  private final PotsMapper potsMapper;

  public PotsService(PotsRepository potsRepository, PotsMapper potsMapper) {
    this.potsRepository = potsRepository;
    this.potsMapper = potsMapper;
  }

  public List<PotsResponseDTO> findAllPots(Integer accountId) {
    return potsMapper.listToResponse(potsRepository.findAllPotsByAccountId(accountId));
  }
}
