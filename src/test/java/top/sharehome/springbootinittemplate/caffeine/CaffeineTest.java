package top.sharehome.springbootinittemplate.caffeine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.utils.caffeine.LocalCacheUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Caffeine本地缓存测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class CaffeineTest {

    @Test
    public void testCaffeine() {
        LocalCacheUtils.put("1", 1);
        Map<String, Object> map = new HashMap<String,Object>() {
            {
                put("2", 2);
                put("3", 3);
                put("4", 4);
                put("5", 5);
            }
        };
        LocalCacheUtils.putAll(map);
        List<String> all = Arrays.asList("1", "2", "3", "4", "5");

        System.out.println(LocalCacheUtils.get("1"));
        System.out.println(LocalCacheUtils.getAll(Arrays.asList("1", "2", "3", "5")));

        LocalCacheUtils.delete("5");
        System.out.println(LocalCacheUtils.getAll(all));

        LocalCacheUtils.delete(Arrays.asList("1", "2"));
        System.out.println(LocalCacheUtils.getAll(all));

        LocalCacheUtils.clear();
        System.out.println(LocalCacheUtils.getAll(all));
    }

}