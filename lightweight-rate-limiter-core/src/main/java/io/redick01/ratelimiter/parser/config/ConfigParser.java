package io.redick01.ratelimiter.parser.config;

import io.redick01.spi.SPI;

import java.io.IOException;
import java.util.Map;

/**
 * @author Redick01
 */
@SPI("yml")
public interface ConfigParser {

    /**
     * Parse content.
     * @param content content
     * @return k-v properties
     * @throws IOException if occurs error while parsing
     */
    Map<Object, Object> doParse(String content) throws IOException;

    /**
     * Parse content.
     * @param content content
     * @param prefix key prefix
     * @return k-v properties
     */
    Map<Object, Object> doParse(String content, String prefix);
}
