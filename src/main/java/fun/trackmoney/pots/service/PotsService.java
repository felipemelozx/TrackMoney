package fun.trackmoney.pots.service;

import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.mapper.PotsMapper;
import fun.trackmoney.pots.repository.PotsRepository;
import fun.trackmoney.user.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PotsService {

  private final PotsRepository potsRepository;
  private final PotsMapper potsMapper;

  public PotsService(PotsRepository potsRepository, PotsMapper potsMapper) {
    this.potsRepository = potsRepository;
    this.potsMapper = potsMapper;
  }

  @Transactional
  public PotsResponseDTO create(CreatePotsDTO dto, UserEntity currentUser) {
    var entity = potsMapper.toEntity(dto);
    var account = currentUser.getAccount();
    entity.setAccount(account);
    var potsResponse = potsRepository.save(entity);
    return potsMapper.toResponse(potsResponse);
  }

  public List<PotsResponseDTO> findAllPots(UserEntity currentUser) {
    return potsMapper.listToResponse(potsRepository.findAllPotsByAccountId(currentUser.getAccount()));
  }
}
