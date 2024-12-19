package akki697222.forumbot.api;

import akki697222.forumbot.ForumBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandBuilder {
    private JDA jda;
    private String name;
    private SlashCommand command;
    private String description = "";
    private List<OptionData> options;
    public static SlashCommandBuilder create(String name, SlashCommand command, JDA jda) {
        SlashCommandBuilder builder = new SlashCommandBuilder();

        builder.name = name;
        builder.command = command;
        builder.options = new ArrayList<>();
        builder.jda = jda;

        return builder;
    }

    public SlashCommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public SlashCommandBuilder addOption(OptionData data) {
        this.options.add(data);
        return this;
    }

    public void build() {
        ForumBot.commands.put(this.name, this.command);
        SlashCommandData commandData = Commands.slash(this.name, this.description);
        commandData.addOptions(this.options);
        jda.upsertCommand(commandData).queue();
    }
}
