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

package eu.thesimplecloud.mongoinstaller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileEditor {

    private File file;

    private List<String> lines;

    public FileEditor(File file) {
        if (!file.exists()) {
            File dir = new File(file, "..");
            System.out.println();
            dir.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.file = file;
        lines = new ArrayList<String>();
        readFile(file.getAbsolutePath());
    }

    public List<String> getAllLines() {
        return lines;
    }

    public String get(String name) {
        for (String s : lines) {
            String[] array = s.split("=");
            if (array[0].equalsIgnoreCase(name)) {
                return array[1];
            }
        }
        return null;
    }

    public void write(String s) {
        lines.add(s);
    }

    public String getLine(int i) {
        if (i > lines.size() - 1)
            return null;
        return lines.get(i);
    }

    public void set(String name, String value) {
        int line = -1;
        for (int i = 0; i < lines.size(); i++) {
            String s = lines.get(i);
            String[] array = s.split("=");
            if (array[0].equalsIgnoreCase(name)) {
                line = i;
            }
        }
        if (line != -1) {
            lines.remove(line);
        }
        lines.add(name + "=" + value);
    }

    public void save() throws IOException {
        BufferedWriter writer = null;

        writer = new BufferedWriter(new FileWriter(file));
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }

        writer.close();
    }

    private void readFile(String file) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            String str;

            while ((str = in.readLine()) != null) {
                lines.add(str);
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void replaceLine(String line, String replace) {
        for (int i = 0; i < lines.size(); i++) {
            String s = lines.get(i);
            if (s.equalsIgnoreCase(line)) {
                lines.remove(i);
                lines.set(i, replace);
                return;
            }

        }
    }

}
