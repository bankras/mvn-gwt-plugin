# mvn-gwt-plugin

This is a maven plugin for using gwt in a consistent and easy to use manner. Currently
the plugin supports compiling and starting a application is hosted mode. Check the
plugin site for details on configuring the plugin in your project.

The following is a list of open issues. Tasks that can be done to improve the plugin.

* The resource directories need to be added to the classpath for the compile mojo

* Maven test-harnessing

* Compiling should get an extra parameter 'prompt' default-value=false to force
  compiling of all gwt applications, instead of the question

* Rob van Maris is suggesting to add a feature to disable compilation. Like passing the tests with the surefire plugin
