package fun.trackmoney.service;

import fun.trackmoney.common.result.ErrorCode;
import fun.trackmoney.common.result.Result;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Import(ResultRollbackIntegrationTest.RollbackTestService.class)
class ResultRollbackIntegrationTest {

  @Autowired
  private RollbackTestService rollbackTestService;

  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  void whenResultErrReturnedWithRollbackHelperAfterWrite_shouldRollbackDatabaseWrite() {
    String categoryName = "Rollback-Category-" + UUID.randomUUID();

    Result<CategoryEntity> result = rollbackTestService.writeAndRollback(categoryName);

    assertInstanceOf(Result.Err.class, result);
    CategoryEntity persisted = categoryRepository.findAll().stream()
        .filter(c -> categoryName.equals(c.getName()))
        .findFirst()
        .orElse(null);
    assertNull(persisted, "Database write must be rolled back when TransactionRollbackHelper marks transaction");
  }

  @Test
  void whenResultErrReturnedWithoutRollbackAfterWrite_shouldCommitDatabaseWriteByDefault() {
    String categoryName = "Commit-Default-Category-" + UUID.randomUUID();

    Result<CategoryEntity> result = rollbackTestService.writeAndErrWithoutRollback(categoryName);

    assertInstanceOf(Result.Err.class, result);
    CategoryEntity persisted = categoryRepository.findAll().stream()
        .filter(c -> categoryName.equals(c.getName()))
        .findFirst()
        .orElse(null);
    assertNotNull(persisted, "Spring commits writes by default when Result.err() is returned without rollback-only");
    categoryRepository.delete(persisted);
  }

  @Test
  void whenResultOkReturnedAfterWrite_shouldCommitSuccessfully() {
    String categoryName = "Success-Category-" + UUID.randomUUID();

    Result<CategoryEntity> result = rollbackTestService.writeAndSucceed(categoryName);

    assertInstanceOf(Result.Ok.class, result);
    CategoryEntity persisted = categoryRepository.findAll().stream()
        .filter(c -> categoryName.equals(c.getName()))
        .findFirst()
        .orElse(null);
    assertNotNull(persisted, "Normal Result.ok must commit writes to the database");
    categoryRepository.delete(persisted);
  }

  @TestComponent
  static class RollbackTestService {

    private final CategoryRepository categoryRepository;

    RollbackTestService(CategoryRepository categoryRepository) {
      this.categoryRepository = categoryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result<CategoryEntity> writeAndRollback(String name) {
      CategoryEntity category = new CategoryEntity(null, name, "#123456");
      categoryRepository.save(category);
      return TransactionRollbackHelper.rollbackAndErr(ErrorCode.TRANSACTION_NOT_FOUND);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result<CategoryEntity> writeAndErrWithoutRollback(String name) {
      CategoryEntity category = new CategoryEntity(null, name, "#654321");
      categoryRepository.save(category);
      return Result.err(ErrorCode.TRANSACTION_NOT_FOUND);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result<CategoryEntity> writeAndSucceed(String name) {
      CategoryEntity category = new CategoryEntity(null, name, "#00ff00");
      CategoryEntity saved = categoryRepository.save(category);
      return Result.ok(saved);
    }
  }
}
