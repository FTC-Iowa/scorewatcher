/*
 * Copyright 2017 Jeramie Vens.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.firstinspiresiowa.scorewatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jeram
 */
public class Event implements FileEvents {
    private final File file;
    private String eventName;

    public String getEventName() {
        return eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public boolean isMultiDivisions() {
        return multiDivisions;
    }
    
    private String eventType;
    private boolean multiDivisions;
    
    public Event(File _file) {
        file = _file;
        if(file.exists()) {
            parseFile();
        } else {
            eventName = "";
        }
    }
    
    public final void parseFile() {
        String row;
        int i = 0;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            while((row = bufferedReader.readLine()) != null) {
                switch(i) {
                    case 1:
                        eventName = row.replaceAll(" ", "_");
                        break;
                    case 3:
                        eventType = row;
                        break;
                    case 5:
                        multiDivisions = "true".equals(row);
                        break;
                    default:
                        break;
                }
                i++;
            }
            
            bufferedReader.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TeamList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TeamList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void onFileCreate() {
        parseFile();
        String filename = "reports/Rankings_" + App.app.event.getEventName() + ".html";
        App.app.rankings.setFile(new File(App.app.config.getRootDir(), filename));
    }

    @Override
    public void onFileDelete() {
        
    }

    @Override
    public void onFileModify() {
        parseFile();
        String filename = "reports/Rankings_" + App.app.event.getEventName() + ".html";
        App.app.rankings.setFile(new File(App.app.config.getRootDir(), filename));
    }

    
}
