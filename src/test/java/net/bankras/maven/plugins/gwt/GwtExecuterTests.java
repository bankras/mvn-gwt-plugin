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

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roald Bankras
 */
public class GwtExecuterTests extends TestCase {

    public void testExecute() throws Exception {
        List<String> command = new ArrayList<String>();
//        command.add("java.exe");
//        command.add("-Xdebug");
//        command.add("-Xnoagent");
//        command.add("-Djava.compiler=NONE");
//        command.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5006");
//        command.add("nl.jteam.maven.plugins.gwt.Tester");
//        command.add("com.google.gwt.dev.GWTShell");
//        command.add("-port zzz");
//        command.add("-noserver");
//        command.add("-whitelist \"\"");
//        command.add("-blacklist \"\"");
//        command.add("-logLevel ALL");
//        command.add("-out");
//        command.add("\\development\\projects\\maven-sandbox\\maven-gwt-plugin\\target\\test-classes");
        command.add("-style");
        command.add("detailed");
//        command.add("detailed");
//        command.add("nl.osix.dsm.gwt.AdminApplication/AdminApplication.html");
        String classpath = "CLASSPATH=\\development\\projects\\maven-sandbox\\maven-gwt-plugin\\target\\test-classes;\\development\\tools\\gwt-windows-1.3.3\\gwt-dev-windows.jar;\\development\\localrepo\\org\\gwtwidgets\\gwt-widgets-server\\0.1.1\\gwt-widgets-server-0.1.1.jar;\\development\\localrepo\\com\\google\\gwt\\gwt-user\\1.3.3\\gwt-user-1.3.3.jar";
        String gwtHomeEnv = "GWT_HOME=\\development\\tools\\gwt-windows-1.4.10";

//        GwtExecuter.execute(command);

//        Process p = Runtime.getRuntime().exec((String[])command.toArray(new String[command.size()]), new String[] { classpath, gwtHomeEnv });
//        GWTShell.main((String[])command.toArray(new String[command.size()]));

//        int processStatus;
//        while(true) {
//            try {
//                processStatus = p.exitValue();
//                break;
//            } catch (IllegalThreadStateException itse) {
//                //** process is not yet finished
////                    System.out.println(".");
//            }
//            String output = new BufferedReader(new InputStreamReader(p.getErrorStream())).readLine();
//            if(output != null) {
//                System.out.println(output);
//            }
//        }
    }
}
