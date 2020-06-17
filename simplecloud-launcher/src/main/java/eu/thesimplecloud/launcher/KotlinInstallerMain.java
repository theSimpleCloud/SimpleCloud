/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.launcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

public class KotlinInstallerMain {

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, URISyntaxException, ClassNotFoundException {
        System.out.println("Installing kotlin...");
        URLClassLoader urlClassLoader = initClassLoader("1.3.72");
        Thread.currentThread().setContextClassLoader(urlClassLoader);

        String kotlinLauncherMainClassName = LauncherMainKt.class.getName();
        Class<?> launcherMainClass = Class.forName(kotlinLauncherMainClassName, true,  urlClassLoader);
        launcherMainClass.getDeclaredMethod("main", String[].class).invoke(null, (Object) args);
    }

    private static URLClassLoader initClassLoader(String kotlinVersion) throws IOException, URISyntaxException {
        File kotlinStandardLibrary = new File("storage/kotlin/kotlin-stdlib-" + kotlinVersion + ".jar");
        File kotlinJdk8StandardLibrary = new File("storage/kotlin/kotlin-stdlib-jdk8-" + kotlinVersion + ".jar");
        File kotlinJdk7StandardLibrary = new File("storage/kotlin/kotlin-stdlib-jdk7-" + kotlinVersion + ".jar");
        installDependency("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/" + kotlinVersion + "/kotlin-stdlib-" + kotlinVersion + ".jar", kotlinStandardLibrary);
        installDependency("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/" + kotlinVersion + "/kotlin-stdlib-jdk8-" + kotlinVersion + ".jar", kotlinJdk8StandardLibrary);
        installDependency("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk7/" + kotlinVersion + "/kotlin-stdlib-jdk7-" + kotlinVersion + ".jar", kotlinJdk7StandardLibrary);
        return new URLClassLoader(new URL[]{
                kotlinJdk7StandardLibrary.toURI().toURL(),
                kotlinJdk8StandardLibrary.toURI().toURL(),
                kotlinStandardLibrary.toURI().toURL(),
                getRunningJarFile().toURI().toURL()
        }, null);
    }

    private static void installDependency(String downloadLink, File file) throws IOException {
        if (!file.exists()) {
            new JavaDownloader().download(downloadLink, file);
        }
    }

    private static File getRunningJarFile() throws URISyntaxException {
        return new File(KotlinInstallerMain.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    }

}
