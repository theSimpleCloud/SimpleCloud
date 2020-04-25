package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.startup.Launcher
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 17.04.2020
 * Time: 00:17
 */
class ConsoleCompleter(val consoleManager: ConsoleManager) : Completer {

    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {
        val commandString = line.line()

        if (commandString.isEmpty()) return

        val suggestions = consoleManager.commandManager.getAvailableTabCompleteArgs(commandString, consoleManager.consoleSender)
        if (suggestions.isEmpty()) {
            return
        }

        var responses = ArrayList<String>()
        responses.addAll(suggestions)

        if (responses.isNotEmpty()) {
            Collections.sort(responses)
            candidates.addAll(responses.map { Candidate(it) })
        }
    }

}