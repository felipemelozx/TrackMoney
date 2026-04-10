package fun.trackmoney.seed.service.generator;

import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.enums.Frequency;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.entity.RecurringEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RecurringGenerator {

  public List<RecurringEntity> generate(AccountEntity account, List<CategoryEntity> categories) {
    List<RecurringEntity> recurring = new ArrayList<>();

    addSalaryRecurring(account, categories, recurring);
    addHousingRecurring(account, categories, recurring);
    addSubscriptionRecurring(account, categories, recurring);
    addGroceryRecurring(account, categories, recurring);
    addTransportRecurring(account, categories, recurring);

    return recurring;
  }

  private void addSalaryRecurring(AccountEntity account, List<CategoryEntity> categories,
                                  List<RecurringEntity> recurring) {
    var salaryCategory = findCategory(categories, "Salário");
    salaryCategory.ifPresent(category ->
        recurring.add(createRecurring(
            account, category, Frequency.MONTHLY, TransactionType.INCOME,
            4500.0, "Salário Mensal", 5
        ))
    );
  }

  private void addHousingRecurring(AccountEntity account, List<CategoryEntity> categories,
                                   List<RecurringEntity> recurring) {
    var housingCategory = findCategory(categories, "Moradia");
    housingCategory.ifPresent(category ->
        recurring.add(createRecurring(
            account, category, Frequency.MONTHLY, TransactionType.EXPENSE,
            1500.0, "Aluguel", 10
        ))
    );
  }

  private void addSubscriptionRecurring(AccountEntity account, List<CategoryEntity> categories,
                                        List<RecurringEntity> recurring) {
    var subscriptionCategory = findCategory(categories, "Assinaturas");
    subscriptionCategory.ifPresent(category ->
        recurring.add(createRecurring(
            account, category, Frequency.MONTHLY, TransactionType.EXPENSE,
            49.90, "Netflix", 15
        ))
    );
  }

  private void addGroceryRecurring(AccountEntity account, List<CategoryEntity> categories,
                                   List<RecurringEntity> recurring) {
    var groceryCategory = findCategory(categories, "Supermercado");
    groceryCategory.ifPresent(category ->
        recurring.add(createRecurring(
            account, category, Frequency.MONTHLY, TransactionType.EXPENSE,
            600.0, "Compras Mensais", 20
        ))
    );
  }

  private void addTransportRecurring(AccountEntity account, List<CategoryEntity> categories,
                                     List<RecurringEntity> recurring) {
    var transportCategory = findCategory(categories, "Transporte");
    transportCategory.ifPresent(category ->
        recurring.add(createRecurring(
            account, category, Frequency.MONTHLY, TransactionType.EXPENSE,
            300.0, "Combustível", 25
        ))
    );
  }

  private RecurringEntity createRecurring(AccountEntity account, CategoryEntity category,
                                         Frequency frequency, TransactionType type,
                                         double amount, String description, int dayOfMonth) {
    RecurringEntity recurring = new RecurringEntity();
    recurring.setAccount(account);
    recurring.setCategory(category);
    recurring.setFrequency(frequency);
    recurring.setTransactionType(type);
    recurring.setAmount(BigDecimal.valueOf(amount));
    recurring.setTransactionName(description);
    recurring.setDescription(description + " - recorrente");
    recurring.setNextDate(LocalDateTime.now().plusMonths(1).withDayOfMonth(dayOfMonth).withHour(9).withMinute(0));

    return recurring;
  }

  private java.util.Optional<CategoryEntity> findCategory(List<CategoryEntity> categories, String name) {
    return categories.stream()
        .filter(c -> c.getName().equals(name))
        .findFirst();
  }
}
