package akki697222.forumbot.commands;

import akki697222.forumbot.api.SlashCommand;
import akki697222.forumbot.database.ForumThread;
import akki697222.forumbot.database.ForumThreadData;
import akki697222.forumbot.database.ThreadInfomations;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

import static akki697222.forumbot.ForumBot.*;

public class CreateThreadCommand implements SlashCommand {
    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        String name = event.getOption("name").getAsString();
        String startMessage = event.getOption("message").getAsString();
        if (ThreadInfomations.getThreadDatas().size() >= MAX_THREADS) {
            event.reply("スレッドの最大数を超えています。使われていないスレッドを削除するか、管理者に連絡してください。").setEphemeral(true).queue();
            return;
        }
        event.getChannel().asTextChannel().createThreadChannel(name).queue(thread -> {
            ForumThreadData data = new ForumThreadData();
            List<ForumThread> threads = new ArrayList<>(ThreadInfomations.getThreadDatas());
            threads.add(new ForumThread(name, thread.getId(), event.getUser().getId()));
            data.setThreads(threads);
            ThreadInfomations.saveThreadData(data);
            SendCommand.processMessage(thread, startMessage, event.getUser()).thenAccept(finalMessage -> {
                thread.sendMessage(finalMessage).queue();
                event.reply("スレッド '" + name + "' が作成されました！").setEphemeral(true).queue();
            }).exceptionally(e -> {
                event.reply("メッセージの処理中にエラーが発生しました。").setEphemeral(true).queue();
                return null;
            });
        }, failure -> {

        });
        messageLogger.info("チャンネル '{}' にて '{}' さんがスレッド '{}' を作成しました。", event.getChannel().getName(), event.getUser().getName(), name);
    }
}
