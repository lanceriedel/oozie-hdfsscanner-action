package com.hivedata.oozie.hdfsscanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HDFSFileTools {
    final public static Logger logger = Logger.getLogger(HDFSFileTools.class
            .getName());

    private Configuration configuration;
    static private boolean tryingToConnectToRemoteFS = false;


    public HDFSFileTools(Configuration configuration) {
        this.configuration = configuration;
    }

    synchronized public List<String> scan(String path, String match,
                                                     boolean recursive) throws IOException {
        Set<String> currentScanIndexes = new HashSet<String>();

        try {

            List<String> updatedRemoteIndexesByName = new ArrayList<String>();
            runGetRemoteFS(updatedRemoteIndexesByName, path, match, recursive);
            currentScanIndexes.addAll(updatedRemoteIndexesByName);

            logger.log(Level.INFO, "Number Indexes remote:"
                    + updatedRemoteIndexesByName.size());

            return updatedRemoteIndexesByName;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not talk to hdfs:["
                    + configuration.get("fs.default.name") + "]", e);
        }

        return null;
    }

    protected void scanRemote(FileSystem fs,
                              List<String> indexesByName, FileStatus file,
                              String match, boolean recursive) throws IOException {
        logger.log(Level.FINE, "Scanning remote (DFS) for known indexes...");
        // Path rootPath = new Path(configuration.get("fs.default.name") + "/"
        // + rpath);
        Path path1 = file.getPath();
        FileStatus[] remoteListing = getRemoteIndexListing(path1, fs);

        if (remoteListing != null) {
            for (int i = 0; i < remoteListing.length; i++) {
                if (remoteListing[i].isDir() && recursive) {
                    scanRemote(fs, indexesByName, remoteListing[i], match,
                            recursive);
                } else {
                    org.apache.hadoop.fs.Path path = remoteListing[i].getPath();
                    String name = path.toString();
                    if (match == null || match.isEmpty()
                            || name.contains(match)) {

                        logger.log(Level.FINE, "Scan.. found remote: " + name
                                + " full:"
                                + remoteListing[i].getPath().toString());

                        indexesByName.add(name);
                        logger.log(Level.FINE, "Adding new index name:" + name);
                    }
                }
            }
        } else {
            logger.log(Level.INFO, "No remote files found in scan...");

        }
    }


    protected FileStatus[] getRemoteIndexListing(Path rootPath, FileSystem fs)
            throws IOException {

        logger.log(Level.FINE, "Remote index listing at:" + rootPath);

        try {

            FileStatus[] files = fs.listStatus(rootPath, new PathFilter() {
                @Override
                public boolean accept(Path path) {

                    return true;
                }
            });
            // fs.close(); //DONT CLOSE! Closes cached connectins that
            // others
            // are using!

            // logger.log(Level.INFO, "Found remote files listing at:" +
            // rootPath);
            // for (int i = 0; i < files.length; i++) {
            // logger.log(Level.INFO, "->:" +
            // files[i].getPath().toString());///
            //
            // }
            return files;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving HDFS file listing", e);
        }

        return null;

    }

    // /////////////////

    protected void runGetRemoteFS(List<String> indexesByName,
                                  String filepath, String match, boolean recursive) {
        if (tryingToConnectToRemoteFS) {
            logger.log(Level.WARNING,
                    "Still retrying to connect from a previous try to FS:["
                            + configuration.get("fs.default.name") + "]");

            return;
        }

        try {
            FileSystem fs = FileSystem.get(configuration);
            getFs(fs, indexesByName, filepath, match, recursive);

            //asyncGetFs(fs, indexesByName, filepath, match, recursive, executor);
            //fs.close();

        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to run asyncCache update", e);
        }
    }


    protected synchronized int getFs(final FileSystem fsystem,
                                     final List<String> indexesByName, final String filepath,
                                     final String match, final boolean recursive) throws IOException {
        final int[] complete = new int[]{0};

        logger.log(Level.FINE, "Instantiating HDFS connection ["
                + configuration.get("fs.default.name") + "]");
        tryingToConnectToRemoteFS = true;
        logger.log(Level.FINE, "Connected to HDFS connection ["
                + configuration.get("fs.default.name") + "]");

        Path rootPath = new Path(configuration.get("fs.default.name")
                + "/" + filepath);

        FileStatus[] remoteListing = getRemoteIndexListing(rootPath,
                fsystem);
        for (FileStatus fs : remoteListing) {
            if (recursive)
                scanRemote(fsystem, indexesByName, fs, match, recursive);
            else {

                org.apache.hadoop.fs.Path path = fs.getPath();
                String name = path.toString();
                if (match == null || match.isEmpty()
                        || name.contains(match)) {
                    logger
                            .log(Level.FINE, "Scan.. found remote: "
                                    + name + " full:"
                                    + fs.getPath().toString());


                    indexesByName.add(name);
                    logger.log(Level.FINE, "Adding new index name:"
                            + name);
                }
            }
        }
        tryingToConnectToRemoteFS = false;

        return 0;

    }

}
