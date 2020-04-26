package eu.thesimplecloud.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class JavaDownloader {

    public void download(String url, File file) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile != null) parentFile.mkdirs();
        URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0");
        urlConnection.connect();
        Files.copy(urlConnection.getInputStream(), Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
    }

}
