package fun.trackmoney.pots.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_pots")
public class PotsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long potId;

  private String name;
  private String description;
  private Long targetAmount;
  private Long currentAmount;



}
