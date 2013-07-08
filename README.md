oozie-hdfsscanner-action
========================
Motivation:<br/>
Flume  places data into a directory on HDFS and rolls files based on a configuration.

Jobs that are triggered from oozie should work on the new files that have arrived since the last files were processed.
This project is a work in progess to try to solve this problem.


Details:<br/>
The project contains a workflow action that kicks off some custom java code to scan what files to be included in the workflow.
This is all done by using a convention in how file names are formatted (e.g. hdfs://localhost:8020/flume/test/2013/05/09/00/08/FlumeData.1368058096702 )
This is configured in Flume for writing, then in a FileNameFormatter impl for reading/parsing.

Right now all files in the last N minutes - triggered by oozie, are processed (see todo).


TODO:<br/>
Write down a file that is the timestamp of the last file to be processed in a ./tickets directory.
This will be read to see what was last processed and filter during the scan all files that precede (and including) this file timestamp.



