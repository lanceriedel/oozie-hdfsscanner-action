package com.hivedata.oozie.hdfsscanner;

/**
 * Created with IntelliJ IDEA.
 * User: lance
 * Date: 5/9/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */

public interface FileNameFormatter {
    public FileMeta parse(String filePath);
}
