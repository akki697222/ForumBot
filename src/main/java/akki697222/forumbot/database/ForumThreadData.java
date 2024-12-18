package akki697222.forumbot.database;

import java.util.ArrayList;
import java.util.List;

public class ForumThreadData {
    private List<ForumThread> threads;

    public ForumThreadData() {
        this.threads = new ArrayList<>();
    }

    public List<ForumThread> getThreads() {
        return threads;
    }

    public void setThreads(List<ForumThread> threads) {
        this.threads = threads;
    }
}
