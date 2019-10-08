package eu.thesimplecloud.launcher.console.setup.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SetupQuestion(val property: String, val question: String)