oozie-hdfsscanner-action
========================
Motivation:<br/>
Flume  places data into a directory on HDFS and rolls files based on a configuration.

Jobs that are triggered from oozie should work on the new files that have arrived since the last files were processed.
This project is a work in progess to try to solve this problem.


Details:<br/>
See the ./sample-workflow directory of how this all comes together.

The project contains a workflow action that kicks off some custom java code to scan what files to be included in the workflow.
This is all done by using a convention in how file names are formatted (e.g. hdfs://localhost:8020/flume/test/2013/05/09/00/08/FlumeData.1368058096702 )
This is configured in Flume for writing, then in a FileNameFormatter impl for reading/parsing.

Right now all files in the last N minutes - triggered by oozie, are processed (see todo).


The java code can hand off what files to process using the following:



    String outputProp = System.getProperty("oozie.action.output.properties");
    if (outputProp == null)
        outputProp = "/tmp/oozie.properties";
    File file = new File(outputProp);
    Properties props = new Properties();
    props.setProperty("INPUTFILES", matchedFilesStr.toString());
    if (matchedFiles.size()>0)
        props.setProperty("HASFILES", "YES");
    else
        props.setProperty("HASFILES", "NO");

    OutputStream os = new FileOutputStream(file);
    props.store(os, "");


This allows you to do things like this in oozie:
<pre>
    <property>
        <name>mapred.input.dir</name>
        <value>${wf:actionData('hdfs-scan')['INPUTFILES']}</value>
    </property>
</pre>


Note, the mapper job is a complete hack, but just to show how the handoff would occur in the oozie workflow.

TODO:<br/>
Write down a file that is the timestamp of the last file to be processed in a ./tickets directory.
This will be read to see what was last processed and filter during the scan all files that precede (and including) this file timestamp.

Tests!



