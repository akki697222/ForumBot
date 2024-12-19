package akki697222.forumbot;

import akki697222.forumbot.api.CommandOption;
import akki697222.forumbot.api.SlashCommand;
import akki697222.forumbot.api.SlashCommandBuilder;
import akki697222.forumbot.commands.CloseThreadCommand;
import akki697222.forumbot.commands.CreateThreadCommand;
import akki697222.forumbot.commands.SendCommand;
import akki697222.forumbot.database.ThreadInfomations;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class ForumBot {
    public static JDA jda = null;
    public static Map<String, SlashCommand> commands = new HashMap<>();
    public static Logger logger = LoggerFactory.getLogger(ForumBot.class);
    public static Logger messageLogger = LoggerFactory.getLogger("ForumBot Messages");
    public static int MAX_THREADS;
    public static void main(String[] args) {
        Options options = new Options();

        Option tokenOption = new Option("t", "token", true, "Discord bot token");
        Option threadsOption = new Option("T", "max-threads", true, "Max Creatable Threads");
        tokenOption.setRequired(true);
        options.addOption(tokenOption);
        options.addOption(threadsOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp("DiscordBot", options);
            System.exit(1);
        }

        String token = cmd.getOptionValue("token");
        MAX_THREADS = Integer.parseInt(cmd.getOptionValue("max-threads", "30"));

        try {
            logger.info("Starting ForumBot...");

            ThreadInfomations.setup();

            jda = JDABuilder.createDefault(token)
                    .setRawEventsEnabled(true)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new Listener())
                    .build();

            jda.awaitReady();

            SlashCommandBuilder.create("send", new SendCommand(), jda)
                    .setDescription("匿名のメッセージを送信します")
                    .addOption(CommandOption.of(
                            "message", "メッセージ", OptionType.STRING)
                            .setRequired(true)
                            .build())
                    .addOption(CommandOption.of(
                            "attachment", "添付ファイル", OptionType.ATTACHMENT)
                            .build())
                    .build();

            SlashCommandBuilder.create("thread", new CreateThreadCommand(), jda)
                    .setDescription("スレッドを開始します")
                    .addOption(CommandOption.of(
                            "name", "スレッドの名前", OptionType.STRING)
                            .setRequired(true)
                            .build())
                    .addOption(CommandOption.of("message", "スレッドの開始メッセージ", OptionType.STRING)
                            .setRequired(true)
                            .build())
                    .build();

            SlashCommandBuilder.create("close", new CloseThreadCommand(), jda)
                    .setDescription("スレッドを閉じます")
                    .build();

            logger.info("Successfully Started!");
        } catch (Exception e) {
            logger.error("Error while running bot", e);
        }
    }
}