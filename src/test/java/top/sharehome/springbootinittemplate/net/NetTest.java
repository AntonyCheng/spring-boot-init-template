package top.sharehome.springbootinittemplate.net;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.utils.net.NetUtils;

/**
 * 测试网络工具
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class NetTest {

    @Test
    public void testGetRegionByIp() {
        String ipStr = "1.2.3.4";
        String regionByIp = NetUtils.getRegionByIp(ipStr);
        System.out.println(regionByIp);
    }

}
