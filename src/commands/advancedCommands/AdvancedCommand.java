package commands.advancedCommands;

import commands.Command;

public abstract class AdvancedCommand extends Command {
	public AdvancedCommand(int numberOfParameters) {
		super(numberOfParameters, "advanced");
	}
}
