package akki697222.forumbot.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class ForumBotLoggerFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if ("ForumBot Messages".equals(event.getLoggerName())) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
}