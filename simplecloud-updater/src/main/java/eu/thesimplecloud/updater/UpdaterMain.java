/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

}
