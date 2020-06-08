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


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoSocketOpenException;
import com.mongodb.client.MongoDatabase;
import eu.thesimplecloud.mongoinstaller.installer.IInstaller;
import eu.thesimplecloud.mongoinstaller.installer.UniversalInstaller;
import eu.thesimplecloud.mongoinstaller.question.UserQuestion;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * Date: 07.06.2020
 * Time: 16:30
 *
 * @author Frederick Baier
 */
public class MongoInstallerMain {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            List<String> charSequences = Arrays.stream(InstallerEnum.values()).map(installerEnum -> installerEnum.name())
                    .collect(Collectors.toList());
            System.out.println("Allowed versions are: " + String.join(", ", charSequences));
            return;
        }

        String enumString = args[0];
        InstallerEnum installerEnum = InstallerEnum.valueOf(enumString);
        printHeader(installerEnum);

        String adminUserPassword = new UserQuestion("Please provide the password for your admin user").waitForResult();
        String databaseName = new UserQuestion("Please provide the name for the new database").waitForResult();
        String userName = new UserQuestion("Please provide the name for the new user").waitForResult();
        String userPassword = new UserQuestion("Please provide the password for the new user").waitForResult();

        IInstaller installer = new UniversalInstaller();

        System.out.println("The installation begins now. This can take a while.");
        installer.install(installerEnum);
        System.out.println("Installation completed.");
        Thread.sleep(1000);
        System.out.println("Setting up user.");

        try {
            createAdminUser(adminUserPassword);
        } catch (MongoSocketOpenException e) {
            //ignore exception
        }

        editConfigFile();

        restartMongo();

        try {
            createUser(adminUserPassword, databaseName, userName, userPassword);
        } catch (MongoSocketOpenException e) {
            //ignore exception
        }


        System.out.println("Creating info file.");
        createInfoFile(adminUserPassword, databaseName, userName, userPassword);
    }

    private static void createInfoFile(String adminUserPassword, String databaseName, String userName, String userPassword) throws IOException {
        File theFile = new File("mongo.txt");
        FileEditor editor = new FileEditor(theFile);
        editor.set("adminUserName", "admin");
        editor.set("adminDatabase", "admin");
        editor.set("adminUserPassword", adminUserPassword);
        editor.set("databaseName", databaseName);
        editor.set("userName", userName);
        editor.set("userPassword", userPassword);
        editor.save();
    }

    private static void printHeader(InstallerEnum installerEnum) {
        System.out.println("\n" +
                "                                 _____           _        _ _           \n" +
                "  /\\/\\   ___  _ __   __ _  ___   \\_   \\_ __  ___| |_ __ _| | | ___ _ __ \n" +
                " /    \\ / _ \\| '_ \\ / _` |/ _ \\   / /\\/ '_ \\/ __| __/ _` | | |/ _ \\ '__|\n" +
                "/ /\\/\\ \\ (_) | | | | (_| | (_) /\\/ /_ | | | \\__ \\ || (_| | | |  __/ |   \n" +
                "\\/    \\/\\___/|_| |_|\\__, |\\___/\\____/ |_| |_|___/\\__\\__,_|_|_|\\___|_|   \n" +
                "                    |___/                    ");
        System.out.println("Installer: " + installerEnum.name());
    }

    private static void createUser(String adminPassword, String newDatabaseName, String newUserName, String newUserPassword) {
        MongoClientURI uri = new MongoClientURI("mongodb://admin:" + adminPassword + "@localhost:27017/admin");
        MongoClient mongo = new MongoClient(uri);
        final MongoDatabase db = mongo.getDatabase(newDatabaseName);
        final BasicDBObject createUserCommand = new BasicDBObject("createUser", newUserName)
                .append("pwd", newUserPassword).append("roles",
                        Collections.singletonList(
                                new BasicDBObject("role", "readWrite").append("db", newDatabaseName)
                        ));
        db.runCommand(createUserCommand);
    }

    private static void restartMongo() throws IOException, InterruptedException {
        Runtime.getRuntime().exec("sudo systemctl restart mongod").waitFor();
    }

    private static void editConfigFile() throws IOException {
        File file = new File("/etc/mongod.conf");
        //File file = new File("mongod.conf");
        FileEditor editor = new FileEditor(file);
        editor.replaceLine("#security:", "security:\n" +
                "    authorization: enabled");
        editor.replaceLine("  bindIp: 127.0.0.1", "  bindIp: 0.0.0.0");
        editor.save();
    }

    private static void createAdminUser(String adminUserPassword) {
        MongoClient mongo = new MongoClient("localhost", 27017);
        final MongoDatabase db = mongo.getDatabase("admin");
        final BasicDBObject createUserCommand = new BasicDBObject("createUser", "admin")
                .append("pwd", adminUserPassword)
                .append("roles",
                        Arrays.asList(
                                new BasicDBObject("role", "userAdmin").append("db", "admin"),
                                new BasicDBObject("role", "dbAdmin").append("db", "admin"),
                                new BasicDBObject("role", "userAdminAnyDatabase").append("db", "admin"),
                                new BasicDBObject("role", "dbAdminAnyDatabase").append("db", "admin"),
                                new BasicDBObject("role", "clusterAdmin").append("db", "admin"),
                                new BasicDBObject("role", "dbOwner").append("db", "admin")
                        ));
        db.runCommand(createUserCommand);
    }

}
