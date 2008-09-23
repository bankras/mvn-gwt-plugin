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
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Roald Bankras
 * @goal compile
 * @phase compile
 * @requiresDependencyResolution compile
 */

public class CompileMojo extends AbstractGwtMojo {

    private final static String GWT_CLASS = "com.google.gwt.dev.GWTCompiler";

    protected List<String> createCommand() throws MojoExecutionException {

        List<String> command = new ArrayList<String>();
        command.add("java");
        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(getCompileTimeClasspath());
        command.add(GWT_CLASS);
//            command.add("-port 8888");
//            command.add("-noserver");
//            command.add("-whitelist \"\"");
//            command.add("-blacklist \"\"");
        command.add("-logLevel");
        command.add(logLevel);
        command.add("-gen");
        command.add(project.getBasedir() + File.separator + "target" + File.separator + "gwt-gen");
        command.add("-out");
        command.add(outputDirectory);
        command.add("-style");
        command.add(gwtStyle);
        return command;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!skip) {
            this.getLog().info("Using GWT_HOME <" + gwtHome + ">");
            List<String> command = createCommand();
            List<String> applications = findGwtApplications();
            this.getLog().debug("Spawning process: ");
            for (String application : applications) {
                command.add(application);
                if (this.getLog().isDebugEnabled()) {
                    for (String arg : command) {
                        this.getLog().debug("                " + arg);
                    }
                }
                int status = GwtExecuter.execute(command);
                if (status != 0) {
                    throw new MojoFailureException("The GWT process failed.");
                }
                command.remove(application);
            }
        } else {
            this.getLog().warn("Skipping GWT plugin");
        }
    }
}