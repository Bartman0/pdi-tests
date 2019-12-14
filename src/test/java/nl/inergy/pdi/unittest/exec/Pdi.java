package nl.inergy.pdi.unittest.exec;

import java.io.IOException;

public class Pdi {
    // we expect to start in /data-integration
    public static final String ENTRYPOINT_CMD = "../entrypoint.sh";
    public static String cmdFormat = ENTRYPOINT_CMD + " %s %s";

    public static void runJob(String jobName) throws IOException {
        String cmd = String.format(cmdFormat, "run_job", jobName);
        Process p = new ProcessBuilder(cmd).inheritIO().start();
    }

    public static void runTransformation(String transformationName) throws IOException {
        String cmd = String.format(cmdFormat, "run_trans", transformationName);
        Process p = new ProcessBuilder(cmd).inheritIO().start();
    }
}
