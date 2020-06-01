package sktest.spring.test;

import org.junit.Test;
import org.shaneking.spring.test.SKSpringMvcUnit;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = SpringUnitApplication.class)
public class SKSpringMvcUnitTest extends SKSpringMvcUnit {
  @Test
  public void empty() {
    tstPrint("empty");
  }
}
