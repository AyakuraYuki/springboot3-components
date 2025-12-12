package cc.ayakurayuki.spring.components.utility.office;

import cc.ayakurayuki.spring.components.utility.platform.OSUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jodconverter.core.office.InstalledOfficeManagerHolder;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.office.LocalOfficeManager;

/**
 * Office 插件工具
 * <p>
 * 这个工具本身设计成交给Spring管理的组件，目前不启动，所以相关注解都注释了
 *
 * @author Ayakura Yuki
 * @date 2025/12/12-10:36
 */
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class OfficePluginUtils {

    private LocalOfficeManager officeManager;

    // @Value("${office.plugin.server.ports:2001,2002}")
    private String serverPorts;

    // @Value("${office.plugin.task.timeout:5m}")
    private String timeout;

    // @Value("${office.plugin.task.taskexecutiontimeout:5m}")
    private String taskExecutionTimeout;

    // @Value("${office.plugin.task.maxtasksperprocess:5}")
    private int maxTasksPerProcess;

    /// 启动office组件
    //@PostConstruct
    public void startOfficeManager() throws OfficeException {
        File officeHome = LocalOfficeUtils.getDefaultOfficeHome();
        if (officeHome == null) {
            return;
        }
        boolean kill = killProcess();
        if (kill) {
            log.warn("detected running office process(es), killed before running a new office");
        }
        try {
            String[] portsS = serverPorts.split(",");
            int[] ports = Arrays.stream(portsS).mapToInt(Integer::parseInt).toArray();
            long timeout = 100000L; // DurationStyle.detectAndParse(timeout).toMillis(); // 实际要用到Spring的工具
            long taskExecutionTimeout = 100000L; // DurationStyle.detectAndParse(taskExecutionTimeout).toMillis(); // 实际要用到Spring的工具
            officeManager = LocalOfficeManager.builder()
                    .officeHome(officeHome)
                    .portNumbers(ports)
                    .processTimeout(timeout)
                    .maxTasksPerProcess(maxTasksPerProcess)
                    .taskExecutionTimeout(taskExecutionTimeout)
                    .build();
            officeManager.start();
            InstalledOfficeManagerHolder.setInstance(officeManager);
        } catch (OfficeException e) {
            log.error("failed to start office, please make sure you have installed one (LibreOffice or OpenOffice)", e);
            throw e;
        }
    }

    //@PreDestroy
    public void destroyOfficeManager() {
        if (officeManager != null && officeManager.isRunning()) {
            log.info("shutting down office manager...");
            OfficeUtils.stopQuietly(officeManager);
        }
    }

    private boolean killProcess() {
        boolean flag = false;
        String stdout;
        try {
            if (OSUtils.IS_OS_WINDOWS) {
                Process p = Runtime.getRuntime().exec("cmd /c tasklist");
                stdout = retrieveStdout(p);
                if (stdout.contains("soffice.bin")) {
                    Runtime.getRuntime().exec("taskkill /im soffice.bin /f");
                    flag = true;
                }
            } else if (OSUtils.IS_OS_MAC || OSUtils.IS_OS_MAC_OSX) {
                Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ps -ef | grep soffice.bin | grep -v grep"});
                stdout = retrieveStdout(p);
                if (StringUtils.ordinalIndexOf(stdout, "soffice.bin", 3) > 0) {
                    Runtime.getRuntime().exec(new String[]{"sh", "-c", "kill -15 `ps -ef | grep soffice.bin | grep -v grep | awk 'NR==1{print $2}'`"});
                    flag = true;
                }
            } else {
                Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ps -ef | grep soffice.bin | grep -v grep | wc -l"});
                stdout = retrieveStdout(p);
                if (!stdout.startsWith("0")) {
                    Runtime.getRuntime().exec(new String[]{"sh", "-c", "ps -ef | grep soffice.bin | grep -v grep | awk '{print \"kill -9 \"$2}' | sh"});
                    flag = true;
                }
            }
        } catch (IOException e) {
            log.error("failed to kill office process", e);
        }
        return flag;
    }

    private String retrieveStdout(Process p) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream(); InputStream is = p.getInputStream()) {
            byte[] b = new byte[256];
            while (is.read(b) > 0) {
                os.write(b);
            }
            os.flush();
            return os.toString();
        } catch (IOException e) {
            return "";
        }
    }

}
