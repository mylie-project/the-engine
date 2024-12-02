package mylie.util.configuration;

import java.io.InputStream;
import java.io.OutputStream;

public interface ConfigurationSerializer {
    void deserialize(Configuration<?> configuration, InputStream inputStream);

    void serialize(Configuration<?> configuration, OutputStream outputStream);
}
