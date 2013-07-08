package com.hivedata.oozie.hdfsscanner;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: lance
 * Date: 5/9/13
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */



public class FlumeFileNameFormatter implements FileNameFormatter {
    // e.g.: hdfs://localhost:8020/flume/test/2013/05/09/00/08/FlumeData.1368058096702
    public static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy/MM/dd/HH/mm");


    @Override
    public FileMeta parse(String filePath) {
        String[] args = filePath.split("/");

        FileMeta meta = new FileMeta();

        try {
            /**
             * Turns out that the timestamp is when the Flume writes the file
             * so if there is a backlog catching up, the dates will be off.
             * So taking the folder name for this.
             */
            String minute = args[args.length -2];
            String hour = args[args.length -3];
            String day = args[args.length -4];
            String month = args[args.length -5];
            String year = args[args.length -6];

            Date d = dateFormat.parse(year+"/"+month+"/"+day+"/"+hour+"/"+minute);
            meta.setTimestamp(d);
            meta.setFileName(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing filename: " + filePath, e);
        }
        // System.out.println(Arrays.toString(args));
        // TODO Auto-generated method stub
        meta.setFileName(filePath);
        meta.setId("");
        return meta;
    }

}