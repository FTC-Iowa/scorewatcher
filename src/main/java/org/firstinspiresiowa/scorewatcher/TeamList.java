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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author jeram
 */
public class TeamList  implements FileEvents{
    private final File file;
    private final JSONArray teamArray;
    
    public TeamList(File _file) {
        file = _file;
        teamArray = new JSONArray();
        if(file.exists()) {
            parseFile();
        }
        
        try {
            App.app.dirWatcher.registerFile(this);
        } catch (IOException ex) {
            Logger.getLogger(TeamList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public JSONArray getTeamList() {
        return teamArray;
    }
    
    public final void parseFile() {
        String row;
        int i = 0;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            while((row = bufferedReader.readLine()) != null) {
                parseRow(i, row);
                i++;
            }
            
            bufferedReader.close();
            
            App.app.log("Team List", teamArray.toJSONString());
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TeamList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TeamList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void parseRow(int i, String row) {
        String cols[] = row.split("\\|");
        int number = Integer.parseInt(cols[1]);
        String name = cols[2].trim();
        String city = cols[3].trim();
        String state = cols[4].trim();
        String country = cols[5].trim();
        JSONObject team = new JSONObject();
        team.put("name", name);
        team.put("number", number);
        team.put("city", city);
        team.put("state", state);
        team.put("country", country);
        if(i < teamArray.size()) {
            teamArray.set(i, team);
        } else {
            teamArray.add(i, team);
        }
        
        App.app.log("Parsed Team", team.toJSONString());
    }

    @Override
    public void onFileCreate() {
        parseFile();
    }

    @Override
    public void onFileDelete() {
        
    }

    @Override
    public void onFileModify() {
        parseFile();
    }
    
    public File getFile() {
        return file;
    }
}
