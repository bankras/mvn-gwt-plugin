/*
 *  Copyright 2008 Roald Bankras
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bankras.maven.plugins.gwt;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roald Bankras
 * @goal shell
 * @requiresDependencyResolution runtime
 */
public class ShellMojo extends AbstractGwtMojo {

    private final static String GWT_CLASS = "com.google.gwt.dev.GWTShell";


    /**
     * @parameter expression="${gwt.debug}" default-value="true"
     */
    protected boolean debug;

    /**
     * @parameter expression="${gwt.debug.port}" default-value="5005"
     */
    protected String debugPort;

    protected List<String> createCommand() throws MojoExecutionException {
        List<String> command = new ArrayList<String>();
        command.add("java.exe");
        if (debug) {
            command.add("-Xdebug");
            command.add("-Xnoagent");
            command.add("-Djava.compiler=NONE");
            command.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + debugPort);
        }
        command.add("-cp");
        command.add(getRunTimeClasspath());
        command.add(GWT_CLASS);
//            command.add("-port 8888");
//            command.add("-noserver");
//            command.add("-whitelist \"\"");
//            command.add("-blacklist \"\"");
        command.add("-logLevel");
        command.add(logLevel);
        command.add("-gen");
        command.add(project.getBuild().getOutputDirectory() + File.separator + "gwt-gen");
        command.add("-out");
        command.add(project.getBuild().getOutputDirectory() + File.separator + "gwt-out");
        command.add("-style");
        command.add(gwtStyle);
        String targetApplication = handleGwtApplications(findGwtApplications());
        String targetClassname = targetApplication.substring(targetApplication.lastIndexOf('.') + 1);
        targetApplication += "/" + targetClassname + ".html";

        command.add(targetApplication);
        return command;
    }


}
