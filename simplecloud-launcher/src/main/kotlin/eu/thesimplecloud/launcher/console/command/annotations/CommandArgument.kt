package eu.thesimplecloud.launcher.console.command.annotations

import eu.thesimplecloud.launcher.console.command.provider.DefaultCommandSuggestionProvider
import eu.thesimplecloud.launcher.console.command.provider.ICommandSuggestionProvider
import kotlin.reflect.KClass

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 14:46
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandArgument(val name: String, val suggestionProvider: KClass<out ICommandSuggestionProvider> = DefaultCommandSuggestionProvider::class) {
}