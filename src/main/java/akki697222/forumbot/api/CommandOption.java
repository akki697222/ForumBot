package akki697222.forumbot.api;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandOption {
    private String name;
    private String description;
    private OptionType optionType;
    private boolean isRequired = false;
    private boolean isAutocomplete = false;

    /**
     * Create a new option for {@link SlashCommandBuilder}
     */
    public static CommandOption of(String name, String description, OptionType type) {
        CommandOption option = new CommandOption();

        option.description = description;
        option.name = name;
        option.optionType = type;

        return option;
    }

    /**
     * Changes option is required for command execution
     */
    public CommandOption setRequired(boolean required) {
        this.isRequired = required;
        return this;
    }

    public CommandOption setAutocomplete(boolean autocomplete) {
        this.isAutocomplete = autocomplete;
        return this;
    }

    /**
     * Build option and returns {@link OptionData}
     */
    public OptionData build() {
        return new OptionData(optionType, name, description, isRequired, isAutocomplete);
    }
}
