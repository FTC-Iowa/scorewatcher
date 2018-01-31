/*
 * Copyright 2018 Jason.
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 *
 * @author Jason
 */
public class Awards implements FileEvents{
    private File file;
    
    JSONArray awardsArray;
    
    public Awards(File _file) {
        file = _file;
        awardsArray = new JSONArray();
        if(file.exists()){
            parseFile();
        }
        
        App.app.dirWatcher.registerFile(this);
    }
    
    public JSONArray getAwardsList () {
        return awardsArray;
    }
    
    private void parseFile() {
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
            
            App.app.log("Awards", awardsArray.toJSONString());
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TeamList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TeamList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void parseRow(int i, String row) {
        int c = 0;
        String cols[] = row.split("\\|",-1);
        String awardName = cols[c++];
        int teamsPerAward = Integer.parseInt(cols[c++]);
        c++; //boolean requiredAward = "true".equals(cols[c++]);
        boolean order = "true".equals(cols[c++]);
        String awardDescription = cols[c++];
        String script = cols[c++];
        boolean notPresentedToTeam = Boolean.getBoolean(cols[c++]);
        String nameOfWinner = cols[c++];
        int j = 0;
        int[] teamArray = new int[teamsPerAward];
        while (j < teamsPerAward){
            teamArray[j] = Integer.parseInt(cols[c++]);
            j++;
        }
        
        JSONObject awards = new JSONObject();
        
        awards.put("teams per award", teamsPerAward);
        awards.put("name", awardName);
        awards.put("description", awardDescription);
        awards.put("order", order);
        awards.put("script", script);
        if(notPresentedToTeam){
            awards.put("winner", nameOfWinner);
        }
        else{
            awards.put("winner", teamArray);
        }
        
        
        if(i < awardsArray.size())
            awardsArray.set(i, awards);
        else
            awardsArray.add(i, awards);
        App.app.log("Parsed Match", awards.toJSONString());
    }

    @Override
    public File getFile() {
        return file;
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
}
