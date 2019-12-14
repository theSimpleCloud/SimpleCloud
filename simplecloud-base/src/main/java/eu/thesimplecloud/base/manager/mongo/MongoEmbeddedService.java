package eu.thesimplecloud.base.manager.mongo;

import de.flapdoodle.embed.mongo.*;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.NamedOutputStreamProcessor;
import de.flapdoodle.embed.process.runtime.Network;
import org.apache.commons.lang3.StringUtils;
import ru.yandex.qatools.embed.service.AbstractEmbeddedService;
import ru.yandex.qatools.embed.service.LogWatchStreamProcessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;

import static de.flapdoodle.embed.mongo.distribution.Version.Main.*;
import static de.flapdoodle.embed.process.io.Processors.*;
import static java.lang.Integer.parseInt;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static jodd.io.FileUtil.delete;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * Embedded MongoDB service
 *
 * @author smecsia
 */
public class MongoEmbeddedService extends AbstractEmbeddedService {
    private static final String HOST_PORT_SPLIT_PATTERN = "(?<!:):(?=[123456789]\\d*$)";
    public static final int INIT_TIMEOUT_MS = 30000;
    public static final String REPLSET_OK_TOKEN_2 = "replSet PRIMARY";
    public static final String REPLSET_OK_TOKEN_3 = "transition to primary complete";
    public static final String USER_ADDED_TOKEN = "Successfully added user";
    public static final String WIRED_TIGER = "wiredTiger";
    private MongodProcess mongod;
    private final String replicaSet;
    private final String host;
    private final int port;
    private final String mongoDBName;
    private final String username;
    private final String password;
    private MongodStarter runtime;
    private MongodExecutable executable;
    private IMongodConfig mongodConfig;
    private IRuntimeConfig runtimeConfig;
    private final String replSetName;
    private LogWatchStreamProcessor mongodOutput;
    private String[] roles = {"\"readWrite\"", "{\"db\":\"local\",\"role\":\"read\"}"};
    private String adminUsername = "admin";
    private String adminPassword = "admin";
    private boolean useWiredTiger = false;
    private boolean useAuth = false;
    private Version.Main useVersion = Version.Main.PRODUCTION;
    private String authMechanisms = "MONGODB-CR";
    private int oplogSizeMb = 100;

    public MongoEmbeddedService(String replicaSet, String mongoDatabaseName) throws IOException {
        this(replicaSet, mongoDatabaseName, null, null, "local", null, true, 10000);
        useAuth(false);
    }

    public MongoEmbeddedService(String replicaSet, String mongoDatabaseName,
                                String mongoUsername, String mongoPassword, String replSetName) throws IOException {
        this(replicaSet, mongoDatabaseName, mongoUsername, mongoPassword, replSetName, null, true, 10000);
    }

    public MongoEmbeddedService(String replicaSet,
                                String mongoDatabaseName,
                                String mongoUsername,
                                String mongoPassword,
                                String replSetName,
                                String dataDirectory,
                                boolean enabled,
                                int initTimeout
    ) throws IOException {
        super(dataDirectory, enabled, initTimeout);
        this.username = mongoUsername;
        this.password = mongoPassword;
        this.mongoDBName = mongoDatabaseName;
        this.replicaSet = replicaSet;
        this.replSetName = replSetName;
        final String[] replSetEl = replicaSet.split(",")[0].split(HOST_PORT_SPLIT_PATTERN);
        this.host = replSetEl[0];
        this.port = parseInt(replSetEl[1]);
        useAuth(!isEmpty(username) && !isEmpty(password));
    }

    public MongoEmbeddedService withOplogSize(int oplogSizeMb) {
        this.oplogSizeMb = oplogSizeMb;
        return this;
    }

    public MongoEmbeddedService useAuth(boolean auth) {
        this.useAuth = auth;
        return this;
    }

    public MongoEmbeddedService useVersion(Version.Main useVersion) {
        this.useVersion = useVersion;
        return this;
    }

    public MongoEmbeddedService useAuthMechanisms(String mechanisms) {
        this.authMechanisms = mechanisms;
        return this;
    }

    public MongoEmbeddedService useWiredTiger() {
        this.useWiredTiger = true;
        return this;
    }

    @Override
    public void doStart() {
        mongodOutput = new LogWatchStreamProcessor(
                format(isMongo3() ? REPLSET_OK_TOKEN_3 : REPLSET_OK_TOKEN_2),
                Collections.<String>emptySet(),
                silent());
        runtimeConfig = new RuntimeConfigBuilder()
                .defaults(Command.MongoD)
                .processOutput(new ProcessOutput(
                        mongodOutput,
                        namedConsole("[mongod error]"),
                        console()))
                .build();
        runtime = MongodStarter.getInstance(runtimeConfig);

        try {
            final MongoEmbeddedService self = this;
            getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    self.stop();
                }
            });

            startWithAuth();
            if (newDirectory) {
                addAdmin();
            }
            if (useAuth) {
                addUser();
            }
        } catch (Exception e) {
            logger.error("Failed to startup embedded MongoDB", e);
        }
    }

    private boolean isMongo3() {
        return asList(V3_0, V3_1, PRODUCTION, DEVELOPMENT).contains(useVersion);
    }

    private void startWithAuth() throws IOException {
        prepareExecutable(useAuth);
        startMongoProcess();
    }

    private void startMongoProcess() throws IOException {
        mongod = executable.start();
        if (newDirectory && replSetName != null) {
            try {
                initiateReplicaSet();
            } catch (Exception e) {
                logger.error("Failed to initialize replica set", e);
            }
        }
    }

    private void prepareExecutable(boolean authEnabled) throws IOException {
        final MongoCmdOptionsBuilder cmdBuilder = new MongoCmdOptionsBuilder();
        cmdBuilder.enableAuth(authEnabled);
        if (useWiredTiger && isMongo3()) {
            cmdBuilder.useStorageEngine(WIRED_TIGER);
        }
        final IMongoCmdOptions cmdOptions = cmdBuilder.build();
        MongodConfigBuilder builder = new MongodConfigBuilder()
                .version(useVersion)
                .cmdOptions(cmdOptions)
                .net(new Net(host, port, Network.localhostIsIPv6()));
        if (authEnabled && isMongo3()) {
            builder.setParameter("authenticationMechanisms", authMechanisms);
        }
        if (replSetName != null) {
            removeLockFile(builder);
            builder.replication(new Storage(dataDirectory, replSetName, oplogSizeMb));
        }
        mongodConfig = builder.build();
        executable = null;
        executable = runtime.prepare(mongodConfig);
    }

    private void removeLockFile(MongodConfigBuilder builder) {
        final File lockFile = Paths.get(dataDirectory, "mongod.lock").toFile();
        try {
            delete(lockFile);
        } catch (Exception e) {
            logger.warn("No lock file found for embedded mongodb or removal failed: " + e.getMessage());
        }
    }

    @Override
    public void doStop() {
        if (executable != null) {
            executable.stop();
        }
    }

    public void initiateReplicaSet() throws IOException, InterruptedException {
        final String scriptText = join(asList(
                format("rs.initiate({\"_id\":\"%s\",\"members\":[{\"_id\":1,\"host\":\"%s:%s\"}]});",
                        replSetName, host, port),
                "rs.slaveOk();rs.status();"), "");
        runScriptAndWait(scriptText, null, null, null, null, null);
        mongodOutput.waitForResult(INIT_TIMEOUT_MS);
    }

    private void addAdmin() throws IOException {
        final String scriptText = join(
                format("db.createUser(" +
                                "{\"user\":\"%s\",\"pwd\":\"%s\"," +
                                "\"roles\":[" +
                                "\"root\"," +
                                "{\"role\":\"userAdmin\",\"db\":\"admin\"}," +
                                "{\"role\":\"dbAdmin\",\"db\":\"admin\"}," +
                                "{\"role\":\"userAdminAnyDatabase\",\"db\":\"admin\"}," +
                                "{\"role\":\"dbAdminAnyDatabase\",\"db\":\"admin\"}," +
                                "{\"role\":\"clusterAdmin\",\"db\":\"admin\"}," +
                                "{\"role\":\"dbOwner\",\"db\":\"admin\"}," +
                                "]});\n",
                        adminUsername, adminPassword));
        runScriptAndWait(scriptText, USER_ADDED_TOKEN, new String[]{"couldn't add user", "failed to load", "login failed"}, "admin", null, null);
    }

    private void addUser() throws IOException {
        final String scriptText = join(format("db = db.getSiblingDB('%s'); " +
                        "db.createUser({\"user\":\"%s\",\"pwd\":\"%s\",\"roles\":[%s]});\n" +
                        "db.getUser('%s');",
                mongoDBName, username, password, StringUtils.join(roles, ","), username), "");
        runScriptAndWait(scriptText, USER_ADDED_TOKEN, new String[]{"already exists", "failed to load", "login failed"}, "admin", adminUsername, adminPassword);
    }

    private void runScriptAndWait(String scriptText, String token, String[] failures, String dbName, String username, String password) throws IOException {
        System.out.println(scriptText + " : " + token + " : " + failures + " : " + dbName + " : " + username + " : " + password);
        IStreamProcessor mongoOutput;
        if (!isEmpty(token)) {
            mongoOutput = new LogWatchStreamProcessor(
                    format(token),
                    (failures != null) ? new HashSet<>(asList(failures)) : Collections.<String>emptySet(),
                    silent());
        } else {
            mongoOutput = new NamedOutputStreamProcessor("[mongo shell output]", silent());
        }
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(Command.Mongo)
                .processOutput(new ProcessOutput(
                        mongoOutput,
                        namedConsole("[mongo shell error]"),
                        console()))
                .build();
        MongoShellStarter starter = MongoShellStarter.getInstance(runtimeConfig);
        final File scriptFile = writeTmpScriptFile(scriptText);
        final MongoShellConfigBuilder builder = new MongoShellConfigBuilder();
        if (!isEmpty(dbName)) {
            builder.dbName(dbName);
        }
        if (!isEmpty(username)) {
            builder.username(username);
        }
        if (!isEmpty(password)) {
            builder.password(password);
        }
        starter.prepare(builder
                .scriptName(scriptFile.getAbsolutePath())
                .version(mongodConfig.version())
                .net(mongodConfig.net())
                .build()).start();
        if (mongoOutput instanceof LogWatchStreamProcessor) {
            ((LogWatchStreamProcessor) mongoOutput).waitForResult(INIT_TIMEOUT_MS);
        }
    }

    private File writeTmpScriptFile(String scriptText) throws IOException {
        File scriptFile = File.createTempFile("tempfile", ".js");
        scriptFile.deleteOnExit();
        BufferedWriter bw = new BufferedWriter(new FileWriter(scriptFile));
        bw.write(scriptText);
        bw.close();
        return scriptFile;
    }

    public Net net() {
        return mongodConfig.net();
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String... roles) {
        this.roles = roles;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
