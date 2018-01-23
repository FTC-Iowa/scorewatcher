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
public class Matches implements FileEvents {
    private enum MatchType {
        Practice,
        Qualification,
        Semifinal,
        Final;
        
        public static MatchType parse(int number) {
            switch (number) {
                case 0:
                    return Practice;
                case 1:
                    return Qualification;
                case 2:
                    return Semifinal;
                case 3:
                    return Final;
            }
            return Qualification;
        }
    }
    
    private final File file;
    
    private final JSONArray matchArray;
    
    public Matches(File _file) {
        file = _file;
        matchArray = new JSONArray();
        if(file.exists()) {
            parseFile();
        }
        
        App.app.dirWatcher.registerFile(this);
    }
    
    public JSONArray getMatchList() {
        return matchArray;
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
            
            System.out.println(matchArray.toJSONString());
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TeamList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TeamList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void parseRow(int i, String row) {
        int c = 0;
        String cols[] = row.split("\\|",-1);
        int division = Integer.parseInt(cols[c++]);
        MatchType matchType = MatchType.parse(Integer.parseInt(cols[c++]));
        int number = Integer.parseInt(cols[c++]);
        int matchStartTimeCnt = Integer.parseInt(cols[c++]);
        
        long matchStartTime = 0;
        
        for(int k = 0; k<matchStartTimeCnt; k++) {
            matchStartTime = Long.parseLong(cols[c++]);
        }
        
        
        c++;// dummy = cols[4]
        
        int[] red_team = new int[3];        
        red_team[0] = Integer.parseInt(cols[c++]);
        red_team[1] = Integer.parseInt(cols[c++]);
        red_team[2] = Integer.parseInt(cols[c++]);
        int[] blue_team = new int[3];
        blue_team[0] = Integer.parseInt(cols[c++]);
        blue_team[1] = Integer.parseInt(cols[c++]);
        blue_team[2] = Integer.parseInt(cols[c++]);
        c++;// int red1_state = cols[11] - 0: normal, 1: noshow, 2: DQed
        c++;// int red2_state = cols[12] //
        c++;// int red3_state = cols[13] //
        c++;// bool red1_yellow_card = cols[14]
        c++;// bool red2_yellow_card = cols[15]
        c++;// bool red3_yellow_card = cols[16]
        c++;// int blue1_state = cols[17]
        c++;// int blue2_state = cols[18]
        c++;// int blue3_state = cols[19]
        c++;// int blue1_yellow = cols[20]
        c++;// int blue2_yellow = cols[21]
        c++;// int blue3_yellow = cols[22]
        boolean[] red_surrogate = new boolean[3];
        red_surrogate[0] = "1".equals(cols[c++]);
        red_surrogate[1] = "1".equals(cols[c++]);
        red_surrogate[2] = "1".equals(cols[c++]);
        boolean[] blue_surrogate = new boolean[3];
        blue_surrogate[0] = "1".equals(cols[c++]);
        blue_surrogate[1] = "1".equals(cols[c++]);
        blue_surrogate[2] = "1".equals(cols[c++]);
        boolean saved = "1".equals(cols[c++]);
        
        int red_autoJewlesRemaining = Integer.parseInt(cols[c++]);
        int red_autoGlyphsInCryptobox = Integer.parseInt(cols[c++]);
        int red_autoCryptoboxKeys = Integer.parseInt(cols[c++]);
        int red_autoRobotParked = Integer.parseInt(cols[c++]);
        int red_teleGlyphsScored = Integer.parseInt(cols[c++]);
        int red_teleCompletedRows = Integer.parseInt(cols[c++]);
        int red_teleCompletedColumns = Integer.parseInt(cols[c++]);
        int red_teleCompletedCyphers = Integer.parseInt(cols[c++]);
        int red_endgRelicsInZone1 = Integer.parseInt(cols[c++]);
        int red_endgRelicsInZone2 = Integer.parseInt(cols[c++]);
        int red_endgRelicsInZone3 = Integer.parseInt(cols[c++]);
        int red_endgRelicsUpright = Integer.parseInt(cols[c++]);
        int red_endgRobotsBalanced = Integer.parseInt(cols[c++]);
        int red_minorPenalties = Integer.parseInt(cols[c++]);
        int red_majorPenalties = Integer.parseInt(cols[c++]);
        int red_minorPenaltiesAwarded = Integer.parseInt(cols[c++]);
        int red_majorPenalitesAwarded = Integer.parseInt(cols[c++]);
        
        int blue_autoJewlesRemaining = Integer.parseInt(cols[c++]);
        int blue_autoGlyphsInCryptobox = Integer.parseInt(cols[c++]);
        int blue_autoCryptoboxKeys = Integer.parseInt(cols[c++]);
        int blue_autoRobotParked = Integer.parseInt(cols[c++]);
        int blue_teleGlyphsScored = Integer.parseInt(cols[c++]);
        int blue_teleCompletedRows = Integer.parseInt(cols[c++]);
        int blue_teleCompletedColumns = Integer.parseInt(cols[c++]);
        int blue_teleCompletedCyphers = Integer.parseInt(cols[c++]);
        int blue_endgRelicsInZone1 = Integer.parseInt(cols[c++]);
        int blue_endgRelicsInZone2 = Integer.parseInt(cols[c++]);
        int blue_endgRelicsInZone3 = Integer.parseInt(cols[c++]);
        int blue_endgRelicsUpright = Integer.parseInt(cols[c++]);
        int blue_endgRobotsBalanced = Integer.parseInt(cols[c++]);
        int blue_minorPenalties = Integer.parseInt(cols[c++]);
        int blue_majorPenalties = Integer.parseInt(cols[c++]);
        int blue_minorPenaltiesAwarded = Integer.parseInt(cols[c++]);
        int blue_majorPenalitesAwarded = Integer.parseInt(cols[c++]);
        
        
        int red_autoBonus = 0;
        int red_auto = 30 * red_autoJewlesRemaining + 15 * red_autoGlyphsInCryptobox +
                30 * red_autoCryptoboxKeys + 10 * red_autoRobotParked;
        int red_endGame = 10 * red_endgRelicsInZone1 + 20 * red_endgRelicsInZone2 +
                40 * red_endgRelicsInZone3 + 15 * red_endgRelicsUpright +
                20 * red_endgRobotsBalanced;
        int red_teleop = 2 * red_teleGlyphsScored + 10 * red_teleCompletedRows +
                20 * red_teleCompletedColumns + 30 * red_teleCompletedCyphers;
        int blue_penalties = 10 * red_minorPenalties + 40 * red_majorPenalties;
        
        
        int blue_autoBonus = 0;
        int blue_auto = 30 * blue_autoJewlesRemaining + 15 * blue_autoGlyphsInCryptobox +
                30 * blue_autoCryptoboxKeys + 10 * blue_autoRobotParked;
        int blue_endGame = 10 * blue_endgRelicsInZone1 + 20 * blue_endgRelicsInZone2 +
                40 * blue_endgRelicsInZone3 + 15 * blue_endgRelicsUpright +
                20 * blue_endgRobotsBalanced;
        int blue_teleop = 2 * blue_teleGlyphsScored + 10 * blue_teleCompletedRows +
                20 * blue_teleCompletedColumns + 30 * blue_teleCompletedCyphers;
        int red_penalties = 10 * blue_minorPenalties + 40 * blue_majorPenalties;
        
        JSONObject match = new JSONObject();
        JSONObject red = new JSONObject();
        JSONObject blue = new JSONObject();
        
        JSONArray red_teams = new JSONArray();
        int j;
        for(j=0;j<3;j++) {
            JSONObject t = new JSONObject();
            if(red_team[j] != 0) { 
                t.put("number", red_team[j]);
                t.put("surrogate", red_surrogate[j]);
                red_teams.add(t);
            }
        }
        red.put("teams", red_teams);
        
        
        JSONArray blue_teams = new JSONArray();
        for(j=0;j<3;j++) {
            JSONObject t = new JSONObject();
            if(blue_team[j] != 0) { 
                t.put("number", blue_team[j]);
                t.put("surrogate", blue_surrogate[j]);
                blue_teams.add(t);
            }
        }
        blue.put("teams", blue_teams);

        
        
        
        
        if(saved) {
            red.put("auto_bonus", red_autoBonus);
            red.put("auto", red_auto);
            red.put("teleop", red_teleop);
            red.put("endg", red_endGame);
            red.put("penalties", red_penalties);
            
            blue.put("auto_bonus", blue_autoBonus);
            blue.put("auto", blue_auto);
            blue.put("teleop", blue_teleop);
            blue.put("endg", blue_endGame);
            blue.put("penalties", blue_penalties);
        }
        
        match.put("start", matchStartTime);
        
        match.put("number", number);
        match.put("division", division);
        match.put("type", matchType.toString());
        match.put("red", red);
        match.put("blue", blue);
        
        if(i < matchArray.size())
            matchArray.set(i, match);
        else
            matchArray.add(i, match);
        System.out.println(match.toJSONString());
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
