package fun.trackmoney.seed.service.generator;

import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.seed.service.model.AmountRange;
import fun.trackmoney.seed.util.RandomUtil;
import fun.trackmoney.entity.TransactionEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TransactionGenerator {

  private static final Map<String, AmountRange> CATEGORY_AMOUNTS = Map.ofEntries(
      Map.entry("Alimentação", new AmountRange(15.0, 200.0)),
      Map.entry("Transporte", new AmountRange(4.0, 50.0)),
      Map.entry("Moradia", new AmountRange(800.0, 2500.0)),
      Map.entry("Educação", new AmountRange(50.0, 500.0)),
      Map.entry("Saúde", new AmountRange(20.0, 500.0)),
      Map.entry("Lazer", new AmountRange(30.0, 300.0)),
      Map.entry("Vestuário", new AmountRange(50.0, 400.0)),
      Map.entry("Serviços", new AmountRange(30.0, 200.0)),
      Map.entry("Assinaturas", new AmountRange(20.0, 100.0)),
      Map.entry("Supermercado", new AmountRange(100.0, 600.0)),
      Map.entry("Contas (água, luz, internet)", new AmountRange(50.0, 300.0)),
      Map.entry("Impostos e taxas", new AmountRange(50.0, 500.0)),
      Map.entry("Doações", new AmountRange(20.0, 200.0)),
      Map.entry("Investimentos", new AmountRange(100.0, 1000.0)),
      Map.entry("Salário", new AmountRange(2000.0, 8000.0)),
      Map.entry("Rendimentos", new AmountRange(50.0, 500.0)),
      Map.entry("Outros", new AmountRange(10.0, 100.0))
  );

  private static final List<String> INCOME_CATEGORIES = List.of("Salário", "Rendimentos", "Investimentos");

  private static final Map<String, List<String>> TRANSACTION_NAMES = Map.ofEntries(
      Map.entry("Alimentação", List.of("Almoço", "Jantar", "Lanche", "Café", "Churrasco", "Pizza no final de semana")),
      Map.entry("Transporte", List.of("Uber", "Combustível", "Ônibus", "Metrô", "Estacionamento")),
      Map.entry("Moradia", List.of("Aluguel", "Condomínio", "IPTU")),
      Map.entry("Educação", List.of("Curso online", "Livros", "Faculdade", "Idiomas")),
      Map.entry("Saúde", List.of("Farmácia", "Consulta médica", "Exames", "Academia")),
      Map.entry("Lazer", List.of("Cinema", "Viagem", "Shows", "Games")),
      Map.entry("Vestuário", List.of("Roupas", "Tênis", "Acessórios")),
      Map.entry("Serviços", List.of("Manutenção", "Limpeza", "Encanador")),
      Map.entry("Assinaturas", List.of("Netflix", "Spotify", "Amazon Prime", "Gym")),
      Map.entry("Supermercado", List.of("Compras do mês", "Feira", "Padaria")),
      Map.entry("Contas (água, luz, internet)", List.of("Conta de luz", "Conta de água", "Internet")),
      Map.entry("Impostos e taxas", List.of("IPVA", "Licenciamento", "Taxas bancárias")),
      Map.entry("Doações", List.of("Doação", "Caridade")),
      Map.entry("Investimentos", List.of("Tesouro Direto", "Ações", "FII")),
      Map.entry("Salário", List.of("Salário mensal", "13º salário", "Bônus")),
      Map.entry("Rendimentos", List.of("Dividendos", "Juros", "Cashback")),
      Map.entry("Outros", List.of("Diversos", "Não categorizado"))
  );

  public List<TransactionEntity> generate(AccountEntity account,
                                         List<CategoryEntity> categories,
                                         LocalDateTime startDate,
                                         LocalDateTime endDate) {
    List<TransactionEntity> transactions = new ArrayList<>();

    List<YearMonth> months = getMonthsBetween(startDate, endDate);

    for (YearMonth month : months) {
      int transactionCount = RandomUtil.randomInt(80, 120);

      for (int i = 0; i < transactionCount; i++) {
        TransactionEntity tx = generateTransaction(account, categories, month);
        transactions.add(tx);
      }
    }

    return transactions;
  }

  private TransactionEntity generateTransaction(AccountEntity account,
                                                List<CategoryEntity> categories,
                                                YearMonth month) {
    TransactionType type = RandomUtil.randomBoolean(0.15)
        ? TransactionType.INCOME
        : TransactionType.EXPENSE;

    List<CategoryEntity> matchingCategories = categories.stream()
        .filter(c -> type == TransactionType.INCOME
            ? INCOME_CATEGORIES.contains(c.getName())
            : !INCOME_CATEGORIES.contains(c.getName()))
        .toList();

    CategoryEntity category = matchingCategories.get(
        RandomUtil.randomInt(0, matchingCategories.size() - 1)
    );

    var amount = generateAmount(category.getName(), type);
    var date = RandomUtil.randomDateInMonthWithDistribution(month);

    TransactionEntity tx = new TransactionEntity();
    tx.setAccount(account);
    tx.setCategory(category);
    tx.setTransactionType(type);
    tx.setAmount(amount);
    tx.setTransactionDate(date);
    tx.setTransactionName(generateTransactionName(category.getName()));
    tx.setDescription(generateDescription(category.getName()));

    return tx;
  }

  private BigDecimal generateAmount(String categoryName, TransactionType type) {
    AmountRange range = CATEGORY_AMOUNTS.getOrDefault(categoryName, new AmountRange(10.0, 100.0));
    return RandomUtil.randomBigDecimalWithVariance(range.min(), range.max(), 0.2);
  }

  private String generateTransactionName(String categoryName) {
    List<String> names = TRANSACTION_NAMES.getOrDefault(categoryName, List.of("Transação"));
    return RandomUtil.randomFromList(names.toArray(new String[0]));
  }

  private String generateDescription(String categoryName) {
    return "Transação de " + categoryName + " gerada automaticamente";
  }

  private List<YearMonth> getMonthsBetween(LocalDateTime start, LocalDateTime end) {
    List<YearMonth> months = new ArrayList<>();
    YearMonth current = YearMonth.from(start);
    YearMonth last = YearMonth.from(end);

    while (!current.isAfter(last)) {
      months.add(current);
      current = current.plusMonths(1);
    }

    return months;
  }
}
