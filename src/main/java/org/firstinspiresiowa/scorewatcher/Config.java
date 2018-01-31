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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author vens
 */
public final class Config {
    /// file name of the config file for this event
    public final String FILE_NAME = "firstinspiresiowa.json";
    /// the config file
    private File cfgFile;
    /// ref that all queries to the server contain to id to this event.
    /// This coresponds to the database location /events/{eventId}
    private String eventId;
    /// ref to the divisions the score system is attached to.
    /// This coresponds to the database location /events/{eventId}/divisions/{divisionId}
    private String divisionId;
    /// url of the server we are connected to
    private String server;
    /// server passphrase for login credentials
    private String passphrase;
    /// the root directory of the score system.
    private File rootDir;
    /// the type of event
    private EventType eventType = EventType.LeagueTournament;
    
    public Config() throws FileNotFoundException, IOException{
        rootDir = new File("."); // start by assuming the current directory is the root directory
        cfgFile = new File(rootDir, this.FILE_NAME);
        // see if it exists in the current working directory.  If it doesn't
        // get the correct working directory and switch to it.
        if(!cfgFile.exists()) {
            // the file didn't exist in the working directory, so let's see if 
            // we are in the correct directory
            rootDir = getScoreSystemDirectory();
            cfgFile = new File(rootDir, this.FILE_NAME);
        }
        
        if(!cfgFile.exists()) {
            throw new FileNotFoundException();
        }
        
        try {
            readConfigFile(this.cfgFile);
        } catch (FileNotFoundException ex) {
            App.app.log("Config", "Could not find config file `" + cfgFile.getAbsolutePath() + "`");
            throw ex;
        } catch (IOException ex) {
            App.app.log("Config", "Error reading config file `" + cfgFile.getAbsolutePath() + "`");
            throw ex;
        }
        
        App.app.log("Config", "Event ID    = "+eventId);
        App.app.log("Config", "Division ID = "+divisionId);
        App.app.log("Config", "Event Type  = "+eventType.toString());
        App.app.log("Config", "Server URL  = "+server);
        App.app.log("Config", "Passphrase  = "+passphrase);
    }
    
    public String getEventId() {
        return eventId;
    }

    public String getDivisionId() {
        return divisionId;
    }
    
    public String getServer() {
        return server;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public File getRootDir() {
        return rootDir;
    }
    
    private void readConfigFile(File file) throws IOException {
        try {
            FileReader fileReader = new FileReader(file);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(fileReader);
            this.eventId = (String) json.get("event");
            this.server = (String) json.get("server");
            this.passphrase = (String) json.get("passphrase");
            this.eventType = EventType.valueOf((String)json.get("event_type"));
            JSONArray divisions = (JSONArray) json.get("divisions");
            if(divisions.size() > 1) {
                int i = 0;
                JRadioButton[] options = new JRadioButton[divisions.size()];
                ButtonGroup group = new ButtonGroup();
                JComponent[] inputs = new JComponent[divisions.size()];
                for(Object o : divisions){
                    String s = (String) o;
                    options[i] = new JRadioButton(s);
                    options[i].setName(s);
                    group.add(options[i]);
                    inputs[i] = options[i];
                    i++;
                }
                
                options[0].setSelected(true);
                
                String[] buttons = {"OK"};
                int result = JOptionPane.showOptionDialog(null, inputs, "Select Division", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
                if (result == JOptionPane.OK_OPTION) {
                    for(JRadioButton b : options) {
                        if(b.isSelected()){
                            divisionId = b.getName();
                        }
                    }
                } else {
                    App.app.log("Config", "User canceled / closed the dialog, result = " + result);
                    System.exit(-1);
                }
            } else {
                this.divisionId = (String) divisions.get(0);
            }
            //this.directory = FileSystems.getDefault().getPath((String) json.get("directory"));
        } catch (ParseException ex) {
            App.app.log("Config", "Failed parsing the config file");
            System.exit(-1);            
        }
    }
    
    private File getScoreSystemDirectory() throws FileNotFoundException {
        App.app.log("Config", "Select the root directory of the Score System App...");
        App.app.log("Config", "this is the folder that was created when you extracted the .zip file");
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Score System Directory");
        int rv = fc.showDialog(null, "Select");
        if (rv == JFileChooser.APPROVE_OPTION) {
            fc.getSelectedFile().toPath();
        } else {
            App.app.log("Config", "Failed to open score system directory");
            throw new FileNotFoundException();
        }
        return fc.getSelectedFile();
    }

    public EventType getEventType() {
        return eventType;
    }
}
