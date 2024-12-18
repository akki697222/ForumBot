package akki697222.forumbot.database;

import java.io.Serializable;

public record ForumThread(String name, String id, String ownerId) implements Serializable {

}
