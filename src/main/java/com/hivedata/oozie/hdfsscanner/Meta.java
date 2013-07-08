package com.hivedata.oozie.hdfsscanner;

/**
 * Created with IntelliJ IDEA.
 * User: lance
 * Date: 5/9/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
import java.util.Date;

public interface Meta {

    public String getName();

    public void setName(String indexName);

    public Date getTimestamp();

    public void setTimestamp(Date timestamp);

}
