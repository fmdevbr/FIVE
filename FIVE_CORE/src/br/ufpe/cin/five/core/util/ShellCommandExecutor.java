package br.ufpe.cin.five.core.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Carlos
 */
public class ShellCommandExecutor {

    private static PrintStream errorPrintStream;
    private static PrintStream inputPrintStream;

    public static Boolean execute(String command, String executionDirectory) throws IOException, InterruptedException, ShellCommandExecutorException {
        
        if(!execute(command, new File(executionDirectory))){
            throw new ShellCommandExecutorException("Comando executado com erro: \n" + command + '\n' + executeAndGetResult(command, executionDirectory));
        }
        return Boolean.TRUE;
    }

    public static Boolean execute(String command, File executionDirectory) throws IOException, InterruptedException {       
        boolean isWindows = (System.getProperty("os.name").contains("Windows")) ? Boolean.TRUE : Boolean.FALSE;
       ProcessBuilder pb = new ProcessBuilder(new String[]{ isWindows ? "cmd" : "/bin/sh", isWindows ? "/c" : "-c", command});
        // ProcessBuilder pb = new ProcessBuilder(new String[]{ command });               
        if (isWindows) {
            Map<String, String> env = pb.environment();
            env.put("Path", env.get("Path") + ";" + executionDirectory);
            env.put("CYGWIN", "nodosfilewarning");
        }               
        pb.directory(executionDirectory);
        pb.redirectErrorStream(Boolean.FALSE);
        Process process = pb.start();

        //process.waitFor();

        errorPrintStream = new PrintStream(process.getErrorStream());
        errorPrintStream.start();
        errorPrintStream.interrupt();
        errorPrintStream.join();

        StringBuffer sbError = errorPrintStream.getResult();
        if (sbError.length() != 0) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    public static StringBuffer executeAndGetResult(String command, String executionDirectory) throws IOException, InterruptedException {
        return executeAndGetResult(command, new File(executionDirectory));
    }

    public static StringBuffer executeAndGetResult(String command, File executionDirectory) throws IOException, InterruptedException {
        boolean isWindows = (System.getProperty("os.name").contains("Windows")) ? Boolean.TRUE : Boolean.FALSE;
        ProcessBuilder pb = new ProcessBuilder(new String[]{ isWindows ? "cmd" : "/bin/sh", isWindows ? "/c" : "-c", command});
        if (isWindows) {
            Map<String, String> env = pb.environment();
            env.put("Path", env.get("Path") + ";" + executionDirectory);
            env.put("CYGWIN", "nodosfilewarning");
        }
        pb.directory(executionDirectory);
        pb.redirectErrorStream(Boolean.FALSE);
        Process process = pb.start();

        process.waitFor();

        errorPrintStream = new PrintStream(process.getErrorStream());
        errorPrintStream.start();
//        errorPrintStream.interrupt();
        errorPrintStream.join();

        StringBuffer sbError = errorPrintStream.getResult();
        if (sbError.length() != 0) {
            return sbError;
        } else {
            inputPrintStream = new PrintStream(process.getInputStream());
            inputPrintStream.start();
//            inputPrintStream.interrupt();
            inputPrintStream.join();
            return inputPrintStream.getResult();
        }
    }
}
