Instructions on building your implementation of IRequestInspector, and getting Mockey to consume it. 

Here's what's important.
[MOCKEY_HOME]\plugin\ext        - A place to copy the latest Mockey.jar file. You will manually do this. See below. 
[MOCKEY_HOME]\plugin\lib        - Put any dependencies your implementation needs here.
[MOCKEY_HOME]\plugin\src        - home of your source code. 
[MOCKEY_HOME]\plugin\build.xml  - the Ant build file. You will need Ant installed (version >= 1.6) to run this. 

Pre-requisites:
----------------------------------------------------
1) Get Mockey code from GitHub. Not the Jar file, but a whole code checkout/clone/pull. 
2) Run a build, example:
   > cd [MOCKEY_HOME]
   > ant dist
3) If all goes well, then you should have a the resulting jar file located here [MOCKEY_HOME]\dist\Mockey.jar. You'll 
   need this later on.  


Creating your implementation.
----------------------------------------------------
1) The following steps assume that you're going to use the sample [MOCKEY_HOME]\plugin project to code and build
   your code.   

2) Your implementation of a plugin must do two things:
-- Your class must implement the interface 'com.mockey.plugin.IRequestInspector'. For example, see:
   com.sample.YourFantasticRequestInspector
-- Your implementation package must be also be annotated as @MockeyRequestInspector to let Mockey know where to find 
   your code. For example, see com.sample.package-info.java

3) After you have written your code, it's time to build your Jar. First, you need to copy [MOCKEY_HOME]\dist\Mockey.jar 
   to the [MOCKEY_HOME]\plugin\ext directory for the build to work.   

4) Build your jar via Ant. Ant is going to create <YOUR JAR>.jar file, will include your dependencies (plugin\lib\*), 
   and NOT include any Mockey. The result of your build should be located here: 
   [MOCKEY_HOME]\plugin\dist\<YOUR JAR>.jar 

5) Now it's time to REBUILD Mockey.jar with your plugin. Take your resulting [MOCKEY_HOME]\plugin\dist\<YOUR JAR>.jar 
   and copy it to [MOCKEY_HOME]\web\WEB-INF\lib\<YOUR JAR>.jar
   
6) Re-build Mockey, and start it up:
  >cd [MOCKEY_HOME]\
  >ant dist
  >cd dist
  >java -jar Mockey.jar
  
Result: 
----------------------------------------------------
If all goes well, Mockey should output to the console that it added your implementation to it's plug-in store. Behind
the scenes, Mockey visits all 'package-info.class' items that are annotated with @MockeyRequestInspector, and then 
checks to see if any of those package Class items implement IRequestInspector. 