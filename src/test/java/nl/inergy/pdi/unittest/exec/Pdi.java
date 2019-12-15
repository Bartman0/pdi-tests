package nl.inergy.pdi.unittest.exec;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Pdi {
    // we expect to start in /data-integration; with this relative path it works in- and outside of a Docker container
    public static final String ENTRYPOINT_CMD = "../entrypoint.sh";
    public static String cmdFormat = ENTRYPOINT_CMD + " %s %s";
    public static final String WORKING_DIRECTORY = "./data-integration";

    protected static final Logger logger = LogManager.getLogger(Pdi.class.getName());

    public static void runJob(String jobName) throws IOException, RuntimeException, InterruptedException {
        runPdi(jobName, "run_job", "running job: ", "runJob failed, exit code: ");
    }

    public static void runTransformation(String transformationName) throws IOException, RuntimeException, InterruptedException {
        runPdi(transformationName, "run_trans", "running transformation: ", "runTransformation failed, exit code: ");
    }

    private static void runPdi(String jobName, String runScript, String debugText, String errorText) throws IOException, InterruptedException {
        String[] cmd = new String[]{"sh", "-c", String.format(cmdFormat, runScript, jobName)};
        logger.debug(debugText + String.join(" ", cmd));
        Process p = new ProcessBuilder(cmd).directory(new File(WORKING_DIRECTORY)).inheritIO().start();
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException(errorText + exitCode);
        }
    }
}
