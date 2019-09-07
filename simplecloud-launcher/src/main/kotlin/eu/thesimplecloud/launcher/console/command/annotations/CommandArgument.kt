package eu.thesimplecloud.launcher.console.command.annotations

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 14:46
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandArgument(val name: String) {
}