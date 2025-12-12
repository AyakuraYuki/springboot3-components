package cc.ayakurayuki.spring.components.utility.office;

import cc.ayakurayuki.spring.components.utility.platform.ConfigUtils;
import cc.ayakurayuki.spring.components.utility.platform.OSUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * 本地 LibreOffice/OpenOffice 路径查询工具
 *
 * @author Ayakura Yuki
 * @date 2025/12/12-08:58
 */
public class LocalOfficeUtils {

    private LocalOfficeUtils() {}

    private static final String OFFICE_HOME_KEY = "office.home";
    private static final String DEFAULT_OFFICE_HOME = "default";
    private static final String EXECUTABLE_DEFAULT = "program/soffice.bin";
    private static final String EXECUTABLE_MAC = "program/soffice";
    private static final String EXECUTABLE_MAC_41 = "MacOS/soffice";
    private static final String EXECUTABLE_WINDOWS = "program/soffice.exe";

    public static File getDefaultOfficeHome() {
        Properties properties = new Properties();
        try (InputStream inputStream = LocalOfficeUtils.class.getClassLoader().getResourceAsStream("local-office.properties")) {
            if (inputStream != null) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    properties.load(bufferedReader);
                    ConfigUtils.injectEnvironmentVariablesToProperties(properties);
                }
            }
        } catch (Exception ignored) {
        }

        String officeHome = properties.getProperty(OFFICE_HOME_KEY);
        if (officeHome != null && !officeHome.equals(DEFAULT_OFFICE_HOME)) {
            return new File(officeHome);
        }

        if (OSUtils.IS_OS_WINDOWS) {
            String userDir = ConfigUtils.userDir("");
            // try to find the most recent version of LibreOffice or OpenOffice,
            // starting with the 64-bit version, %ProgramFiles(x86)% on 64-bit
            // machines; %ProgramFiles% on 32-bit ones
            final String programFiles64 = System.getenv("ProgramFiles");
            final String programFiles32 = System.getenv("ProgramFiles(x86)");
            return findOfficeHome(
                    EXECUTABLE_WINDOWS,
                    Paths.get(userDir, "LibreOfficePortable", "App", "libreoffice").toString(),
                    Paths.get(programFiles32, "LibreOffice").toString(),
                    Paths.get(programFiles64, "LibreOffice 7").toString(),
                    Paths.get(programFiles32, "LibreOffice 7").toString(),
                    Paths.get(programFiles64, "LibreOffice 6").toString(),
                    Paths.get(programFiles32, "LibreOffice 6").toString(),
                    Paths.get(programFiles64, "LibreOffice 5").toString(),
                    Paths.get(programFiles32, "LibreOffice 5").toString(),
                    Paths.get(programFiles64, "LibreOffice 4").toString(),
                    Paths.get(programFiles32, "LibreOffice 4").toString(),
                    Paths.get(programFiles32, "OpenOffice 4").toString(),
                    Paths.get(programFiles64, "LibreOffice 3").toString(),
                    Paths.get(programFiles32, "LibreOffice 3").toString(),
                    Paths.get(programFiles32, "OpenOffice.org 3").toString());
        }

        if (OSUtils.IS_OS_MAC) {
            File home = findOfficeHome(
                    EXECUTABLE_MAC_41,
                    "/Applications/LibreOffice.app/Contents",
                    "/Applications/OpenOffice.app/Contents",
                    "/Applications/OpenOffice.org.app/Contents");
            if (home == null) {
                home = findOfficeHome(
                        EXECUTABLE_MAC,
                        "/Applications/LibreOffice.app/Contents",
                        "/Applications/OpenOffice.app/Contents",
                        "/Applications/OpenOffice.org.app/Contents");
            }
            return home;
        }

        // Linux or other *nix variants
        return findOfficeHome(
                EXECUTABLE_DEFAULT,
                "/opt/libreoffice6.0",
                "/opt/libreoffice6.1",
                "/opt/libreoffice6.2",
                "/opt/libreoffice6.3",
                "/opt/libreoffice6.4",
                "/opt/libreoffice7.0",
                "/opt/libreoffice7.1",
                "/opt/libreoffice7.2",
                "/opt/libreoffice7.3",
                "/opt/libreoffice7.4",
                "/opt/libreoffice7.5",
                "/opt/libreoffice7.6",
                "/usr/lib64/libreoffice",
                "/usr/lib/libreoffice",
                "/usr/local/lib64/libreoffice",
                "/usr/local/lib/libreoffice",
                "/opt/libreoffice",
                "/usr/lib64/openoffice",
                "/usr/lib64/openoffice.org3",
                "/usr/lib64/openoffice.org",
                "/usr/lib/openoffice",
                "/usr/lib/openoffice.org3",
                "/usr/lib/openoffice.org",
                "/opt/openoffice4",
                "/opt/openoffice.org3");
    }

    private static File findOfficeHome(final String executablePath, final String... homePaths) {
        return Stream.of(homePaths)
                .filter(homePath -> Files.isRegularFile(Paths.get(homePath, executablePath)))
                .findFirst()
                .map(File::new)
                .orElse(null);
    }

}
