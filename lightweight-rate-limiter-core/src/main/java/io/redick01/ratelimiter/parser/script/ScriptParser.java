package io.redick01.ratelimiter.parser.script;

import io.redick01.spi.SPI;

/**
 * @author Redick01
 */
@SPI
public interface ScriptParser {

    /**
     * get expression value.
     * @param expressKey expression key
     * @param arguments arguments
     * @return expression value
     */
    String getExpressionValue(String expressKey, Object[] arguments);

    /**
     * is lua script.
     * @param script script
     * @return is lua script
     */
    boolean isScript(String script);

    /**
     * get prefix.
     * @param script script
     * @return script prefix
     */
    String getPrefix(String script);
}
