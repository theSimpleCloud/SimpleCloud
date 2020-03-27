package eu.thesimplecloud.updater;


import java.io.*;

public class UpdaterMain {

    /**
     * First argument: delay to wait before replacing
     * Second argument: File to be replaced
     * Third argument: File to replace with
     */
    public static void main(String[] args) {
        try {

            long timeToWait = Long.parseLong(args[0]);
            File fileToBeReplaced = new File(args[1]);
            File fileToReplaceWith = new File(args[2]);
            Thread.sleep(timeToWait);
            while (fileToBeReplaced.exists()) {
                try {
                    fileToBeReplaced.delete();
                } catch (Exception ex) {
                    Thread.sleep(100);
                }
            }
            copyFileUsingStream(fileToReplaceWith, fileToBeReplaced);
            fileToReplaceWith.delete();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

}
