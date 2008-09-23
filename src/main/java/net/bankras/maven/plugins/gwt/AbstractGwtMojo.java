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

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roald Bankras
 */
public abstract class AbstractGwtMojo extends AbstractMojo {
    /**
     * The maven project
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter expression="${gwt.home}"
     * @required
     */
    protected String gwtHome;

    /**
     * The GWT application to be excuted. Specified as the full qualified
     * name of the gwt.xml file, leaving out the double extention (.gwt.xml).
     * <p/>
     * If this variable is left unset, the plugin will search through the classpath
     * to find all applications.
     *
     * @parameter expression="${gwt.app}"
     */
    protected String gwtApp;

    /**
     * Script output style: OBF[USCATED], PRETTY, or DETAILED (defaults to OBF)
     *
     * @parameter expression="${gwt.style}" default-value="obf"
     */
    protected String gwtStyle;

    /**
     * Directory were the compiled class should be placed
     *
     * @parameter expression="${gwt.outputDirectory}" default-value="${basedir}/target/${project.build.finalName}"
     */
    protected String outputDirectory;

    /**
     * The level of logging detail: ERROR, WARN, INFO, TRACE, DEBUG, SPAM, or ALL
     *
     * @parameter expression="${gwt.logLevel}" default-value="WARN"
     */
    protected String logLevel;

    /**
     * @parameter expression="${gwt.skip}" default-value=false
     */
    protected boolean skip;

    /**
     * @parameter
     */
    protected List<String> jvmArgs = new ArrayList<String>();

    public void execute() throws MojoExecutionException, MojoFailureException {
        if(!skip) {
            this.getLog().info("Using GWT_HOME <" + gwtHome + ">");
            List<String> command = createCommand();
            this.getLog().debug("Spawning process: ");
            if (this.getLog().isDebugEnabled()) {
                for (String arg : command) {
                    this.getLog().debug("                " + arg);
                }
            }
            int status = GwtExecuter.execute(command);
            if (status != 0) {
                throw new MojoFailureException("The GWT process failed.");
            }
        } else {
            this.getLog().warn("Skipping GWT plugin");
        }
    }

    protected abstract List<String> createCommand() throws MojoExecutionException;

    protected String handleGwtApplications(List<String> applications) throws MojoExecutionException {
        String targetApplication;
        int appIndex = 0;
        if (applications.size() == 0) {
            throw new MojoExecutionException("No GWT application found.");
        } else if (applications.size() > 1) {
            for (int i = 0; i < applications.size(); i++) {
                System.out.println("(" + (i + 1) + ") " + applications.get(i));
            }
            while (appIndex == 0) {
                System.out.print("Which application do you want to run : ");
                try {
                    String line = new BufferedReader(new InputStreamReader(System.in)).readLine();
                    appIndex = Integer.parseInt(line);
                } catch (NumberFormatException nfe) {
                    appIndex = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            appIndex = 1;
        }
        targetApplication = applications.get(appIndex - 1);
        this.getLog().debug("Starting GWT application " + targetApplication);
        return targetApplication;
    }

    @SuppressWarnings("unchecked")
    protected List<String> findGwtApplications() throws MojoExecutionException {
        List<String> foundApplications = new ArrayList<String>();

        if (gwtApp != null) {
            foundApplications.add(gwtApp);
        } else {
            File sourceDirectory = new File(project.getBuild().getSourceDirectory());
            if (sourceDirectory.isDirectory()) {
                List<String> sourceApplications = new ArrayList<String>();
                findGwtApplications(sourceDirectory, sourceApplications);
                for (String sourceApplication : sourceApplications) {
                    foundApplications.add(trimApplicationPath(sourceApplication, sourceDirectory));
                }
            } else {
                throw new MojoExecutionException("The specified source directory is not a directory.");
            }
            for (Resource resource : (List<Resource>)project.getResources()) {
                File resourceDirectory = new File(resource.getDirectory());
                if (resourceDirectory.isDirectory()) {
                    List<String> resourceApplications = new ArrayList<String>();
                    findGwtApplications(resourceDirectory, resourceApplications);
                    for (String resourceApplication : resourceApplications) {
                        foundApplications.add(trimApplicationPath(resourceApplication, resourceDirectory));
                    }
                } else {
                    throw new MojoExecutionException("The specified resource directory is not a directory.");
                }
            }
        }
        return foundApplications;
    }

    private String trimApplicationPath(String foundApplication, File directory) {
        String targetApplication;
        if (foundApplication.startsWith(directory.getAbsolutePath())) {
            targetApplication = foundApplication.substring(directory.getAbsolutePath().length(), foundApplication.lastIndexOf(".gwt.xml"));
        } else {
            targetApplication = foundApplication.substring(0, foundApplication.lastIndexOf(".gwt.xml"));
        }
        if (targetApplication.startsWith(File.separator)) {
            targetApplication = targetApplication.substring(1);
        }
        targetApplication = targetApplication.replace(File.separator, ".");
        return targetApplication;
    }

    private void findGwtApplications(File sourceDirectory, List<String> foundApplications) {
        File[] list = sourceDirectory.listFiles(new GwtApplicationFilter());
        for (File file : list) {
            if (file.isDirectory()) {
                findGwtApplications(file, foundApplications);
            } else {
                foundApplications.add(file.getAbsolutePath());
                this.getLog().debug("Found GWT application <" + file.getAbsolutePath() + ">");
            }
        }
    }

    private class GwtApplicationFilter implements FileFilter {
        public boolean accept(File pathname) {
            return pathname.isDirectory() || pathname.getName().endsWith(".gwt.xml");
        }
    }

    protected String getCompileTimeClasspath() throws MojoExecutionException {
        String classpath = getProjectClasspath();
        List classpathElements;
        try {
            classpathElements = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Sorry, this is unexpected.", e);
        }
        for (Object classpathElement : classpathElements) {
            classpath += File.pathSeparator + classpathElement;
        }
        return classpath;
    }

    protected String getProjectClasspath() throws MojoExecutionException {
        String classpath = (new StringBuilder()).append(project.getBuild().getSourceDirectory()).append(File.pathSeparator).toString();
        classpath = (new StringBuilder()).append(classpath).append(project.getBuild().getTestOutputDirectory()).append(File.pathSeparator).toString();
        classpath = (new StringBuilder()).append(classpath).append(gwtHome).append(File.separator).append(determineGwtDevOsJar()).append(File.pathSeparator).toString();
        classpath = (new StringBuilder()).append(classpath).append(gwtHome).append(File.separator).append("gwt-user.jar").append(File.pathSeparator).toString();
        for(int i = 0; i < project.getBuild().getResources().size(); i++)
        {
            Object resourcePath = project.getBuild().getResources().get(i);
            classpath = (new StringBuilder()).append(classpath).append(resourcePath).append(File.pathSeparator).toString();
        }
        return classpath;
    }

    protected String getRunTimeClasspath() throws MojoExecutionException {
        String classpath = getProjectClasspath();
        List classpathElements;
        try {
            classpathElements = project.getRuntimeClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Sorry, this is unexpected.", e);
        }
        for (Object classpathElement : classpathElements) {
            classpath += File.pathSeparator + classpathElement;
        }
        return classpath;
    }

    private String determineGwtDevOsJar() {
        getLog().info("Searching for platform dependent implementation");
        File homeDir = new File(gwtHome);
        String[] homeDirContents = homeDir.list();
        for (String homeDirEntry : homeDirContents) {
            getLog().debug("Found " + homeDirEntry);
            if (homeDirEntry.startsWith("gwt-dev-")) {
                getLog().info("Found " + homeDirEntry);
                return homeDirEntry;
            }
        }
        return "";
    }
}
