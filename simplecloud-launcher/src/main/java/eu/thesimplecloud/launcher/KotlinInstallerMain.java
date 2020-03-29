package eu.thesimplecloud.launcher;

import eu.thesimplecloud.api.external.ResourceFinder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class KotlinInstallerMain {

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        File file = downloadKotlinVersionIfNecessary("1.3.71");
        addToClasspath(file);
        LauncherMainKt.main(args);
    }

    private static File downloadKotlinVersionIfNecessary(String kotlinVersion) throws IOException {
        File kotlinFile = new File("storage/kotlin/kotlin-runtime-" + kotlinVersion + ".jar");
        if (!kotlinFile.exists()) {
            System.out.println("Downloading kotlin...");
            new JavaDownloader().download("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/" + kotlinVersion + "/kotlin-stdlib-" + kotlinVersion + ".jar", kotlinFile);
        }
        return kotlinFile;
    }

    public static void addToClasspath(File file) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke((URLClassLoader)ClassLoader.getSystemClassLoader(), file.toURI().toURL());
    }

}
