package akki697222.forumbot.commands;

import akki697222.forumbot.api.SlashCommand;
import akki697222.forumbot.database.ForumThread;
import akki697222.forumbot.database.ForumThreadData;
import akki697222.forumbot.database.ThreadInfomations;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

import static akki697222.forumbot.ForumBot.*;

public class CloseThreadCommand implements SlashCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        ThreadChannel channel = event.getChannel().asThreadChannel();
        messageLogger.info("スレッド '{}' のクローズを　'{}' さんが試行しました。", channel.getName(), event.getUser().getName());
        boolean includesThisChannel = false;
        for (ForumThread thread : ThreadInfomations.getThreadDatas()) {
            if (thread.id().equals(event.getChannelId())) {
                includesThisChannel = true;
            }
        }
        if (!includesThisChannel) {
            event.reply("このチャンネルは作成されたスレッドではない、またはすでに閉じられています。").setEphemeral(true).queue();
            return;
        }
        if (ThreadInfomations.getThreadDatas().stream().anyMatch(t -> t.ownerId().equals(event.getUser().getId())) || event.getGuild().getOwnerId().equals(event.getUser().getId())) {
            ForumThreadData data = new ForumThreadData();
            List<ForumThread> threads = new ArrayList<>(ThreadInfomations.getThreadDatas());
            threads.removeIf(t -> t.id().equals(channel.getId()));
            ThreadInfomations.saveThreadData(data);
            channel.sendMessage("このフォーラムを閉じました。").queue();
            channel.getManager().setArchived(true).setLocked(true).queue();
            event.reply("フォーラムを閉じました。").setEphemeral(true).queue();
        } else {
            event.reply("作成者以外が閉じることはできません。").setEphemeral(true).queue();
        }
    }
}
