This repository is a collection of plugins developed for and deployed with
Keysight Technologies. 

Keysight Technologies is an industry leader providing solutions in the area of
electronic test and measurement. 

These plugins are shared in the hope that either the plugins themselves or
viewing the source can help others to spend less time on their infrastructure
and more time excelling at their passions.

This repository is presently being setup - so much of the documentation is incomplete
as the information is migrated from our internal systems and into bitbucket.  
Several of the plugins are being prepared for the Atlassian Marketplace - others will
be available here in time.

Getting Started
---------------
Prerequisites: The Atlassian SDK

At the top level are folders containing the plugins targeted to the various
Atlassian applications.  Within each are folders which may be intended to
contain a plugin, an instance of the Atlassian application or some arbitrary
data. The general idea is to fire up a particular version of the Atlassian application
in one of the container folders to make it easier to switch between versions.

As an example, to start work on the Confluence plugins with Confluence version
6.2.0.

First, move into the ./confluence/6-2-0 folder and execute **atlas-run**.  That
will bring up an instance of Confluence version 6-2-0 (as defined in the pom.xml).

Next, in another command shell, move into ./confluence and execute **atlas-package**.
That will package all of the plugins as defined in the local directory's pom.xml file.

To develop for one plugin, move into it's directory and execute **atlas-package**.
That will repackage the plugin and Atlassian's quick-reload will bring in the
new version.

To setup a new container, move into ./confluence and execute **atlas-create-confluence-plugin**.
I typically use the group id of com.keysight and the atifact name is the version with 
dashes rather than periods (such as 6-2-0).  Then delete the folders under **test** and **src/main/java**.
Lastly, copy the pom.xml from ./confluence/6-2-0 and update the version number inside.
Then move into the new folder and execute **atlas-run**.  
