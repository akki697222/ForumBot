package akki697222.forumbot.commands;

import akki697222.forumbot.api.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;
import java.util.Random;

public class TestCommand implements SlashCommand {
    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        int range = event.getOption("range").getAsInt();
        event.reply("さいころ: " + new Random().nextInt(range)).queue();
    }
}
