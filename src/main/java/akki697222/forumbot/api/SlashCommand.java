package akki697222.forumbot.api;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public interface SlashCommand {
    void onCommand(SlashCommandInteractionEvent event);
}
