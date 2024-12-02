package mylie.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/// The `EngineVersion` class encapsulates the versioning information for the engine.
/// The versioning information includes details like the engine version, the last Git tag,
/// the commit distance from the last tag, the abbreviated and full Git hashes, the branch name,
/// the clean tag status, and the build time.
/// This information is loaded from a properties file located at
/// `mylie/engine/version.properties`.
@Slf4j
@Getter
public final class BuildInfo {

    final String engineVersion;
    private final String lastTag;
    private final String commitDistance;
    private final String gitHash;
    private final String gitHashFull;
    private final String branchName;
    private final String isCleanTag;
    private final String buildTime;

    /// Constructs an `EngineVersion` object by loading version information from a
    /// properties file.
    /// The properties file is expected to be at
    /// `/com/github/mylie/engine/version.properties`
    /// and should contain the following keys:
    /// - `version`: The version of the engine.
    /// - `lastTag`: The last Git tag.
    /// - `commitDistance`: The distance from the last commit.
    /// - `gitHash`: The abbreviated Git hash.
    /// - `gitHashFull`: The full Git hash.
    /// - `branchName`: The name of the Git branch.
    /// - `isCleanTag`: Indicates if the tag is clean.
    /// - `buildTime`: The time when the build was created.
    ///
    /// @throws IllegalStateException if the version properties file does not exist.
    /// @throws RuntimeException if an I/O error occurs while reading the properties
    /// file.
    public BuildInfo() {
        Properties properties = new Properties();
        try (InputStream versionPropertiesStream = getClass().getResourceAsStream("/mylie/engine/version.properties")) {
            if (versionPropertiesStream == null) {
                throw new IllegalStateException("Version properties file does not exist");
            }
            properties.load(new InputStreamReader(versionPropertiesStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.engineVersion = properties.getProperty("version");
        this.lastTag = properties.getProperty("lastTag");
        this.commitDistance = properties.getProperty("commitDistance");
        this.gitHash = properties.getProperty("gitHash");
        this.gitHashFull = properties.getProperty("gitHashFull");
        this.branchName = properties.getProperty("branchName");
        this.isCleanTag = properties.getProperty("isCleanTag");
        this.buildTime = properties.getProperty("buildTime");
    }

    @Override
    public String toString() {
        return "EngineVersion{" + "engineVersion='" + engineVersion + '\'' + ", lastTag='" + lastTag + '\''
                + ", commitDistance='" + commitDistance + '\'' + ", gitHash='" + gitHash + '\'' + ", gitHashFull='"
                + gitHashFull + '\'' + ", branchName='" + branchName + '\'' + ", isCleanTag='" + isCleanTag + '\''
                + '}';
    }

    /// Logs the build information of the engine, including version, Git hash,
    /// branch name, and
    /// build time. The information is displayed in a stylized ASCII art format.
    public void logBuildInfo(Logger logger) {
        logger.info(".----------------.  .----------------.  .----------------.  .----------------. "
                + " .----------------.");
        logger.info("| .--------------. || .--------------. || .--------------. || .--------------. ||"
                + " .--------------. |");
        logger.info("| | ____    ____ | || |  ____  ____  | || |   _____      | || |     _____    | ||"
                + " |  _________   | |");
        logger.info("| ||_   \\  /   _|| || | |_  _||_  _| | || |  |_   _|     | || |    |_   _|   | ||"
                + " | |_   ___  |  | |");
        logger.info("| |  |   \\/   |  | || |   \\ \\  / /   | || |    | |       | || |      | |     |"
                + " || |   | |_  \\_|  | |");
        logger.info("| |  | |\\  /| |  | || |    \\ \\/ /    | || |    | |   _   | || |      | |     |"
                + " || |   |  _|  _   | |");
        logger.info("| | _| |_\\/_| |_ | || |    _|  |_    | || |   _| |__/ |  | || |     _| |_    | ||"
                + " |  _| |___/ |  | |");
        logger.info("| ||_____||_____|| || |   |______|   | || |  |________|  | || |    |_____|   | ||"
                + " | |_________|  | |");
        logger.info("| |              | || |              | || |              | || |              | ||"
                + " |              | |");
        logger.info("| '--------------' || '--------------' || '--------------' || '--------------' ||"
                + " '--------------' |");
        logger.info("'----------------'  '----------------'  '----------------'  '----------------' "
                + " '----------------'");
        logger.info(
                "Engine Version: {}    Git Hash: {}    Git Branch: {}    BuildTime: {}",
                engineVersion(),
                gitHash(),
                branchName(),
                buildTime());
    }
}
