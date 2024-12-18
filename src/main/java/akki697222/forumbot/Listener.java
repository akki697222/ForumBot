package akki697222.forumbot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static akki697222.forumbot.ForumBot.commands;
import static akki697222.forumbot.ForumBot.jda;

public class Listener extends ListenerAdapter {
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commands.forEach((name, command) -> {
            if (name.equals(event.getName())) {
                command.onCommand(event);
            }
        });
    }
}
