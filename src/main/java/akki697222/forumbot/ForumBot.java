package akki697222.forumbot;

import akki697222.forumbot.api.SlashCommand;
import akki697222.forumbot.api.SlashCommandBuilder;
import akki697222.forumbot.commands.CloseThreadCommand;
import akki697222.forumbot.commands.CreateThreadCommand;
import akki697222.forumbot.commands.SendCommand;
import akki697222.forumbot.database.ThreadInfomations;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ForumBot {
    public static JDA jda = null;
    public static Map<String, SlashCommand> commands = new HashMap<>();
    public static Logger logger = LoggerFactory.getLogger("ForumBot");
    public static Logger messageLogger = LoggerFactory.getLogger("ForumBot Messages");
    public static Properties properties = null;

    public static void main(String[] args) {
        try {
            logger.info("Starting ForumBot...");

            Properties config = new Properties();
            try {
                URL resource = ForumBot.class.getClassLoader().getResource("config.properties");
                config.load(new FileInputStream(resource.getPath()));
            } catch (IOException | NullPointerException e) {
                logger.error("Failed to load config", e);
                return;
            }
            properties = config;

            ThreadInfomations.setup();

            jda = JDABuilder.createDefault(config.getProperty("token"))
                    .setRawEventsEnabled(true)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new Listener())
                    .build();

            jda.awaitReady();

            SlashCommandBuilder.create("send", new SendCommand(), jda)
                    .setDescription("匿名のメッセージを送信します")
                    .addOption("message", "メッセージ", OptionType.STRING, true)
                    .addOption("attachment", "添付ファイル", OptionType.ATTACHMENT, false)
                    .build();

            SlashCommandBuilder.create("thread", new CreateThreadCommand(), jda)
                    .setDescription("スレッドを開始します")
                    .addOption("name", "スレッドの名前", OptionType.STRING, true)
                    .addOption("message", "スレッドの開始メッセージ", OptionType.STRING, true)
                    .build();

            SlashCommandBuilder.create("close", new CloseThreadCommand(), jda)
                    .setDescription("スレッドを閉じます")
                    .build();

        } catch (Exception e) {
            logger.error("Error while running bot", e);
        }
    }
}