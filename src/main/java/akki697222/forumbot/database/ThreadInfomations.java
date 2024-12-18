package akki697222.forumbot.database;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

import static akki697222.forumbot.ForumBot.logger;

public class ThreadInfomations {
    public static void setup() {
        try {
            File file = new File("threads.json");
            if (!file.exists()) {
                if (!file.createNewFile()) logger.error("Failed to create data file.");
                FileWriter writer = new FileWriter(file);
                writer.write("{\"threads\": []}");
                writer.close();
            }
        } catch (IOException e) {
            logger.error("Failed to setup json", e);
        }
    }

    public static List<ForumThread> getThreadDatas() {
        try (Reader reader = new FileReader("threads.json")) {
            Gson gson = new Gson();
            ForumThreadData data = gson.fromJson(reader, ForumThreadData.class);
            return data.getThreads();
        } catch (IOException e) {
            logger.error("Failed to get data", e);
        }
        return null;
    }

    public static void saveThreadData(ForumThreadData data) {
        Gson gson = new Gson();
        try (Writer writer = new FileWriter("threads.json")) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            logger.error("Failed to save data", e);
        }
    }
}
