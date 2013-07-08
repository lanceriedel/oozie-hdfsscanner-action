package com.hivedata.oozie.hdfsscanner;

/**
 * Created with IntelliJ IDEA.
 * User: lance
 * Date: 5/9/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.Date;

public class FileMeta implements Meta {
    Date parsedDate;
    String fileName;
    String id;

    public Date getTimestamp() {
        return parsedDate;
    }

    public void setTimestamp(Date parsedDate) {
        this.parsedDate = parsedDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return null;
    }

    public void setName(String indexName) {

    }

}
