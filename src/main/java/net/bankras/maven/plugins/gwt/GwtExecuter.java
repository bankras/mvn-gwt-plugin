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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Roald Bankras
 */
public class GwtExecuter {

    public static int execute(List<String> command) {
        int processStatus = 0;
        try {
            ProcessBuilder build = new ProcessBuilder(command);
            Process gwtProcess = build.redirectErrorStream(true).start();

            while(true) {
                try {
                    processStatus = gwtProcess.exitValue();
                    break;
                } catch (IllegalThreadStateException itse) {
                    //** process is not yet finished
                }
                String output = new BufferedReader(new InputStreamReader(gwtProcess.getInputStream())).readLine();
                if(output != null) {
                    System.out.println(output);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return processStatus;
    }
}
