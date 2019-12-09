import org.junit.Test;

import java.io.File;

public class Demo {


    @Test
    public void rikka() throws Exception {
        File[] roots = File.listRoots();
        for (File root : roots) {
            System.out.println(root.getName());
        }
    }
}
