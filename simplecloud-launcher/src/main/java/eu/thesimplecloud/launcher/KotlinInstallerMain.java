package eu.thesimplecloud.launcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class KotlinInstallerMain {

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        System.out.println("Installing kotlin...");
        installKotlin("1.3.71");
        LauncherMainKt.main(args);
    }

    private static void installKotlin(String kotlinVersion) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        File kotlinStandardLibrary = new File("storage/kotlin/kotlin-stdlib-" + kotlinVersion + ".jar");
        File kotlinJdk8StandardLibrary = new File("storage/kotlin/kotlin-stdlib-jdk8-" + kotlinVersion + ".jar");
        File kotlinJdk7StandardLibrary = new File("storage/kotlin/kotlin-stdlib-jdk7-" + kotlinVersion + ".jar");
        installDependency("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/" + kotlinVersion + "/kotlin-stdlib-" + kotlinVersion + ".jar", kotlinStandardLibrary);
        installDependency("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/" + kotlinVersion + "/kotlin-stdlib-jdk8-" + kotlinVersion + ".jar", kotlinJdk8StandardLibrary);
        installDependency("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk7/" + kotlinVersion + "/kotlin-stdlib-jdk7-" + kotlinVersion + ".jar", kotlinJdk7StandardLibrary);
    }

    private static void installDependency(String downloadLink, File file) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (!file.exists()) {
            new JavaDownloader().download(downloadLink, file);
        }
        addToClasspath(file);
    }

    public static void addToClasspath(File file) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke((URLClassLoader)ClassLoader.getSystemClassLoader(), file.toURI().toURL());
    }

}
