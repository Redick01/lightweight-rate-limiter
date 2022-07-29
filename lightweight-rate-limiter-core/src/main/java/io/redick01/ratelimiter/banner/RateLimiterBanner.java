package io.redick01.ratelimiter.banner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

/**
 * @author Redick01
 */
@Slf4j
public class RateLimiterBanner implements InitializingBean {

    private static final String LIMITER = " :: Limiter :: ";

    private static final String BANNER = ""
        + "   __ _           _ _            \n"
        + "  / /(_)_ __ ___ (_) |_ ___ _ __ \n"
        + " / / | | '_ ` _ \\| | __/ _ \\ '__|\n"
        + "/ /__| | | | | | | | ||  __/ |   \n"
        + "\\____/_|_| |_| |_|_|\\__\\___|_|   \n";

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info(AnsiOutput.toString(BANNER, "\n", AnsiColor.GREEN, LIMITER,
                AnsiColor.DEFAULT, AnsiStyle.FAINT));
    }
}
