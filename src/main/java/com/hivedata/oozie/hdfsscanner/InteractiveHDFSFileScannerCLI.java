package com.hivedata.oozie.hdfsscanner;


import org.apache.commons.cli.*;

import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: lance
 * Date: 5/9/13
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class InteractiveHDFSFileScannerCLI {

    static int printUsage() {
        System.out.println("InteractiveHDFSFileScannerCLI");
        return -1;
    }

    private static Options options = new Options();

    private static int help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("InteractiveHDFSFileScannerCLI", options);
        return -1;
    }

    public int run(String[] args) throws IOException {
        // CLI
        options.addOption("c", "config", true, "hdfs config [hdfs://pl.desk:8020]");
        options.addOption("l", "lastdirectory", true, "lastdirectory");
        options.addOption("n", "numminutes", true, "number of minutes to look back");

        CommandLineParser parser = new PosixParser();
        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            return help();
        }

        String config = line.getOptionValue("config");
        String lastDirectory = line.getOptionValue("lastdirectory");
        String numMinutes = line.getOptionValue("numminutes");

        HDFSFileScanner scanner = new HDFSFileScanner();
        scanner.scan(config, lastDirectory, Integer.parseInt(numMinutes));
        return 0;
    }

    public static void main(String[] args) {
        try {
            InteractiveHDFSFileScannerCLI cli = new InteractiveHDFSFileScannerCLI();
            cli.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
