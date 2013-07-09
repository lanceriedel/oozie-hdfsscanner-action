package com.hivedata.oozie.hdfsscanner;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.test.MiniHadoopClusterManager;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Test;
import org.junit.Before;

import java.net.URI;
import java.util.Properties;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: lance
 * Date: 7/8/13
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class HDFSFileScannerTest {
    private final String testPath = "/test/2013/05/09/00/10";
    private final String testPath2 = "/test/2013/05/09/00/09";


    MiniDFSCluster cluster;
    @Before
    public void setup() throws Exception {
        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(new Configuration());

        builder.numDataNodes(1).format(true);
        cluster = builder.build();

    }

    @Test
    public void testScanner() throws Exception {

        //cluster.waitActive();
        cluster.getConfiguration(0);
        URI conf = cluster.getURI(0);
        System.out.println("CONF:" + conf);

        Path file =  new Path(testPath2 + "/FlumeData.1368058096702");
        DFSTestUtil.createFile((FileSystem) cluster.getFileSystem(), file, 1000l, (short)1, new Random().nextLong());



        HDFSFileScanner scanner = new HDFSFileScanner();
        Properties props = scanner.scan(cluster.getConfiguration(0), testPath, 5);
        org.junit.Assert.assertTrue("Scan produed coorect file", props.getProperty("INPUTFILES").contains("1368058096702"));
    }

}
