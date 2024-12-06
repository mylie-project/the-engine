package mylie.util.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class ColorConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {
    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        Level level = event.getLevel();
        return switch (level.toInt()) {
            case Level.DEBUG_INT -> ANSIConstants.CYAN_FG;
            case Level.ERROR_INT -> ANSIConstants.BOLD + ANSIConstants.RED_FG; // same as default color scheme
            case Level.WARN_INT -> ANSIConstants.RED_FG; // same as default color scheme
            case Level.INFO_INT -> ANSIConstants.GREEN_FG; // use CYAN instead of BLUE
            default -> ANSIConstants.DEFAULT_FG;
        };
    }
}
