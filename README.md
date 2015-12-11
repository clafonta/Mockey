README

![Image of Mockey]
(https://s3-us-west-1.amazonaws.com/mockeybuilds/mockey_20150609_sample_screenshot.png)

What is Mockey?
===================

A testing tool, used to mock end point web services for the purpose of testing web service client applications. 

Features
====================

 * Use Mockey as a proxy to a real service, to inspect request and response messages
 * Set up Mockey to go through a corporate proxy server, to reach an endpoint web service
 * Support for HTTP/s interactions (even if your corporate proxy server has https as an endpoint web service)
 * Ability to 'play back' conversations for when endpoint services are not available
 * Ability to run EVERYTHING in a sandbox - your application and the endpoint webservices your application is consuming. 

What is Mockey not good at?
====================

 * Large payloads used in conversations, e.g. 10MB per message
 * Transport protocols other than HTTP, e.g. FTP. 
 * Complicated conversations e.g. if X, then call this database, else if Y, call 'rake FOO', else kick-off-Maven. 
 * Solving everything

Getting Started - Quick
====================
A pre-built jar: <https://github.com/clafonta/Mockey/wiki/Downloads>

To build one locally, see the following. 

Via command line, build Mockey via Ant. 
 > ant dist 


After a successful build, do the following to start the app:
> cd dist 
> java -jar Mockey.jar --help

This will start Mockey (within a Jetty container) and fire up your browser pointing at the defaults (port 8080, /home). Use --help for more options. 

For Tomcat users, you'll need to build the app:
> ant webapptomcat

This will build a WAR file, which you can drop into Tomcat and run. You can set the location of Mockey definition file repot as follows: 
> export JAVA_OPTS="-DmockeyDefinitionsRepoHome=/Users/your-username/some-directory"

When Mockey starts up in Tomcat, it will look for the 'mockeyDefinitionsRepoHome' property and read write needed files in that directory. If not defined, it will read write files in the default user directory that Tomcat is started with.

