package akki697222.forumbot.commands;

import akki697222.forumbot.api.SlashCommand;
import akki697222.forumbot.database.ForumThread;
import akki697222.forumbot.database.ForumThreadData;
import akki697222.forumbot.database.ThreadInfomations;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static akki697222.forumbot.ForumBot.*;

public class SendCommand implements SlashCommand {
    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        String message = event.getOption("message").getAsString();
        Message.Attachment attachment = event.getOption("attachment") != null ? event.getOption("attachment").getAsAttachment() : null;

        messageLogger.info("フォーラムまたはチャンネル '{}' にて '{}' さんがメッセージ 「{}」 の送信を試行しました。", event.getChannel().getName(), event.getUser().getName(), message);
        boolean includesThisChannel = false;
        for (ForumThread thread : ThreadInfomations.getThreadDatas()) {
            if (thread.id().equals(event.getChannelId())) {
                includesThisChannel = true;
            }
        }
        if (!includesThisChannel) {
            event.reply("このチャンネルは作成されたスレッドではない、またはこのフォーラムは閉じられています。").setEphemeral(true).queue();
            return;
        }
        ThreadChannel channel = event.getChannel().asThreadChannel();
        String processedMessage = processMessageAnchor(message, channel.getId());

        processMessage(channel, processedMessage, event.getUser()).thenAccept(finalMessage -> {
            if (attachment != null) {
                try {
                    channel.sendMessage(finalMessage).addFiles(FileUpload.fromData(
                            attachment.getProxy().download().get(),
                            attachment.getFileName()
                    )).queue();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else {
                channel.sendMessage(finalMessage).queue();
            }
            event.reply("送信しました。").setEphemeral(true).queue();
        }).exceptionally(e -> {
            event.reply("メッセージの処理中にエラーが発生しました。").setEphemeral(true).queue();
            return null;
        });
    }

    public static String processMessageAnchor(String message, String channelId) {
        Pattern pattern = Pattern.compile(">>\\d+");
        Matcher matcher = pattern.matcher(message);
        StringBuilder sb = new StringBuilder();

        ThreadChannel threadChannel = jda.getThreadChannelById(channelId);
        if (threadChannel == null) return message;

        List<Message> history = threadChannel.getIterableHistory()
                .cache(false)
                .stream()
                .sorted(Comparator.comparing(Message::getTimeCreated))
                .filter(msg -> msg.getAuthor().getId().equals(jda.getSelfUser().getId()))
                .toList();

        while (matcher.find()) {
            String fullMatch = matcher.group();
            String number = fullMatch.substring(2);

            try {
                int targetMessageIndex = Integer.parseInt(number) - 1;
                if (targetMessageIndex >= 0 && targetMessageIndex < history.size()) {
                    Message targetMessage = history.get(targetMessageIndex);
                    String replacement = String.format("[>>%s](%s)", number, targetMessage.getJumpUrl());
                    matcher.appendReplacement(sb, replacement);
                } else {
                    matcher.appendReplacement(sb, fullMatch);
                }
            } catch (NumberFormatException e) {
                matcher.appendReplacement(sb, fullMatch);
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public static CompletableFuture<String> processMessage(ThreadChannel channel, String message, User sender) {
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder sb = new StringBuilder();

        channel.getIterableHistory().queue(history -> {
            int threadMessages = 1;
            for (Message msg : history) {
                if (msg.getAuthor().getId().equals(jda.getSelfUser().getId())) {
                    threadMessages++;
                }
            }
            sb.append(threadMessages).append("\\. ");
            sb.append("緑色の名無し");
            for (ForumThread thread : ThreadInfomations.getThreadDatas()) {
                if (thread.id().equals(channel.getId())) {
                    if (thread.ownerId().equals(sender.getId())) {
                        sb.append("☆");
                    }
                }
            }
            sb.append(new SimpleDateFormat(" yyyy/MM/dd(E) HH:mm:ss.SSS").format(new Date()));
            sb.append("\n").append(message);

            future.complete(sb.toString());
        }, future::completeExceptionally);

        return future;
    }
}
