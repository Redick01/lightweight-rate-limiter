package io.redick01.spi;

/**
 * @author Redick01
 */
@SPI("spi")
public interface ExtensionFactory {

    /**
     * get extension.
     *
     * @param key key
     * @param clazz Class
     * @param <T> type
     * @return extension
     */
    <T> T getExtension(String key, Class<T> clazz);
}
