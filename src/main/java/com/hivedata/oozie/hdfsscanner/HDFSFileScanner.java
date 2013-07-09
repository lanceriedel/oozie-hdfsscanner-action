package com.hivedata.oozie.hdfsscanner;

import org.apache.hadoop.conf.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: lance
 * Date: 5/9/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */

public class HDFSFileScanner {
    final public static Logger logger = Logger.getLogger(HDFSFileScanner.class
            .getName());
    public static FlumeFileNameFormatter flumeFileNameFormatter = new FlumeFileNameFormatter();
    public static FlumeDirectoryFileNameFormatter flumeDirectoryFileNameFormatter = new FlumeDirectoryFileNameFormatter();


    /**
     * Scans all data up to lastdirectory and limits by the number of minutes.
     * Returns all files that are up to that point
     *
     * @param config
     * @param lastdirectory
     * @param numminutes
     * @throws IOException
     */
    public void scan(String config, String lastdirectory, int numminutes) throws IOException {
        Configuration configuration = new Configuration();

        String conf = config;
        logger.info("HDFSFileScanner, config= " + config);

        configuration.set("fs.default.name", conf);
        configuration.set("hadoop.tmp.dir", "/tmp");
        scan(configuration, lastdirectory, numminutes);
    }

    public Properties scan(Configuration configuration, String lastdirectory, int numminutes) throws IOException {

        //TODO: read timestamp of last file processed
        logger.info("HDFSFileScanner, lastdirectory= " + lastdirectory);
        logger.info("HDFSFileScanner, numminutes= " + numminutes);
        if (lastdirectory.startsWith(configuration.get("fs.default.name"))) {
            lastdirectory = lastdirectory.substring(configuration.get("fs.default.name").length());
            logger.info("HDFSFileScanner,TRIMMED lastdirectory= " + lastdirectory);
        }

        HDFSFileTools tools = new HDFSFileTools(configuration);

        //Last directory timestamp
        FileMeta lastmeta = flumeDirectoryFileNameFormatter.parse(lastdirectory);
        logger.info("LASTMETA:" + lastmeta.getFileName() + " : " + lastmeta.getTimestamp().getTime());

        //maybe just cut off to the day/s and scan?
        String trimmedToDayDirectory = lastmeta.getFileName().substring(0, lastmeta.getFileName().length() - 6);
        logger.info("TrimmedToDay-> " + trimmedToDayDirectory);

        List<String> nodes = tools.scan(trimmedToDayDirectory, null, true);

        long targetTimestamp = lastmeta.getTimestamp().getTime();
        long backNMinutes = targetTimestamp - (numminutes * 1000 * 60);

        //Available files
        List<String> matchedFiles = new ArrayList<String>();
        StringBuffer matchedFilesStr = new StringBuffer();
        if (nodes != null) {
            for (String entry : nodes) {
                FileMeta meta = flumeFileNameFormatter.parse(entry);
                logger.info("META:" + meta.getFileName() + " : " + meta.getTimestamp().getTime());
                logger.info("BACKNMIN:" + backNMinutes + " : Target:" + targetTimestamp);
                if (meta.getTimestamp().getTime() >= backNMinutes && meta.getTimestamp().getTime() < targetTimestamp) {
                    matchedFiles.add(meta.getFileName());
                    logger.info("TARGET->" + meta.getFileName());
                    if (matchedFilesStr.length() > 0) matchedFilesStr.append(",");
                    matchedFilesStr.append(meta.getFileName());
                }
            }
        }

        //TODO: write down timestamp of last file processed


        String outputProp = System.getProperty("oozie.action.output.properties");
        if (outputProp == null)
            outputProp = "/tmp/oozie.properties";
        File file = new File(outputProp);
        Properties props = new Properties();


        props.setProperty("INPUTFILES", matchedFilesStr.toString());
        if (matchedFiles.size() > 0)
            props.setProperty("HASFILES", "YES");
        else
            props.setProperty("HASFILES", "NO");

        OutputStream os = new FileOutputStream(file);
        props.store(os, "");
        os.close();
        logger.info("PROP FILE:" + file.getAbsolutePath());
        return props;
    }


    public static void main(String[] args) {
        try {
            String config = args[0];
            String lastdirectory = args[1];
            int numMinutes = Integer.parseInt(args[2]);

            HDFSFileScanner hdfsFileScanner = new HDFSFileScanner();
            hdfsFileScanner.scan(config, lastdirectory, numMinutes);


            //hack, hack, hack - need some time to figure out proper threading issues -
            //there may be none now, i used to have async calls to hdfs

            Thread.sleep(1000);
            logger.info("DONE:");
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for (Thread t : threadSet) {
                logger.info("\n\n");
                logger.info(t.toString());
                // t.interrupt();
                // t.join();
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
