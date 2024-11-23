package main.commands;

import picocli.CommandLine.Command;
import picocli.AutoComplete;
import main.QBoxCommand;

@Command(name = "completion", description = "Generate bash/zsh completion script for all qbox commands and arguments.")
public class CompletionCommand implements Runnable {

    @Override
    public void run() {
        try {
            // Generate the completion script dynamically
            String scriptName = "qbox_completion";

            AutoComplete.main(new String[] {
                "--name=qbox",                        // Name for the script
                "--force",                              // Overwrite the existing script
                QBoxCommand.class.getCanonicalName()  // Main command class
            });

            System.out.printf("Generated completion script: %s%n", scriptName);
            System.out.println("Source it using: source " + scriptName);
        } catch (Exception e) {
            System.err.printf("Error generating completion script: %s%n", e.getMessage());
            e.printStackTrace();
        }
    }
}
