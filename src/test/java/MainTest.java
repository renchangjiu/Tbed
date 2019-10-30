import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MainTest {
    @Test
    public void mainTest() throws Exception {
        String q1 = "0";
        boolean b = StringUtils.isEmpty(q1) || "1".equals(q1);
        System.out.println(b);
    }
}
