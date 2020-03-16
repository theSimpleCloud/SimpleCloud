package eu.thesimplecloud.launcher.console.command.annotations

import eu.thesimplecloud.launcher.console.command.CommandType

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 30.08.2019
 * Time: 19:20
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(val name: String, val commandType: CommandType) {

}