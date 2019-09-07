package eu.thesimplecloud.launcher.console.command.annotations

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 30.08.2019
 * Time: 19:22
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandSubPath(val path: String = "", val description: String = "") {

}