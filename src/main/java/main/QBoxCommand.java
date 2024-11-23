package main;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExecutionException;
import picocli.CommandLine.Help.Ansi;
import main.commands.*;
import main.commands.encryption.*;
import main.commands.sqlite.SQLiteClient;
import main.commands.formatter.FormatterCommand;
import main.commands.logging.*;
@TopCommand
@Command(
        name = "qbox",
        mixinStandardHelpOptions = true,
        version = "qbox 1.0",
        description = "QBox is a native java toolbox.",

        subcommands = {
            CompletionCommand.class,
            EncryptionCommand.class,
            SQLiteClient.class,
            FormatterCommand.class,
            LoggerCommand.class
        }
)
public class QBoxCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("qbox CLI. Use --help to view available commands.");
    }

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new QBoxCommand());
        // Enable ANSI colors for help
        commandLine.setColorScheme(CommandLine.Help.defaultColorScheme(Ansi.AUTO));
        int exitCode;

        try {
            exitCode = commandLine.execute(args);
        } catch (ExecutionException ex) {
            System.err.println(Ansi.AUTO.string("@|bold,red Error:|@ " + ex.getMessage()));
            ex.printStackTrace();
            exitCode = 1;
        }

        System.exit(exitCode);
    }
}
