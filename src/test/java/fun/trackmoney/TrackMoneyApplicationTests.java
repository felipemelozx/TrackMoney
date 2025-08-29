package fun.trackmoney;

import fun.trackmoney.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TrackMoneyApplication.class)
class TrackMoneyApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @BeforeAll
  static void up() {
    EmbeddedRedisConfig.startRedis();
  }

  @AfterAll
  static void down() {
    EmbeddedRedisConfig.stopRedis();
  }

	@Test
	void contextLoads() {
	}

}
