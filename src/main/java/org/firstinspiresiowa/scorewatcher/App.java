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
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFrame;
import org.json.simple.JSONObject;

/**
 *
 */
public final class App implements Runnable{
    private final JSONObject body;
    private final JSONObject division;
    public final Config config;
    public final Server server;
    public final TeamList teamList;
    public final DirWatcher dirWatcher;
    public final Matches matches;
    public final Rankings rankings;
    public final Event event;
    public final ReportsDir reportsDir;
    private final PrintWriter log;
    
    public static App app;
    public App() throws FileNotFoundException, IOException {
        // save the class object to a static var to give everything else access to it.
        app = this;
        
        // format of the timestamp used for the log file
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        
        // Create a new log file with the timestamp included
        this.log = new PrintWriter("firstinspiresiowa_" + dtf.format(now) + ".log", "UTF-8");
        
        try {
            // parse the config file.  This will look for a file named firstinspiresiowa.org
            // and if it can't find it it will allow the user to choose the root directory of
            // the score system
            config = new Config();
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            log("init", "Could not open config file");
            throw ex;
        }
        
        // create a new directory watcher which will look for changes in the root
        // directory.
        dirWatcher = new DirWatcher();
        
        // build the https JSON object and put the key information into it
        body = new JSONObject();
        division = new JSONObject();
        /// @todo change data to division so we can add other event data such as awards
        body.put("data", division); 
        body.put("eventId", config.getEventId());
        body.put("divisionId", config.getDivisionId());
        body.put("passphrase", config.getPassphrase());
        
        // Save the name of this division.  The data object will be saved in the
        // datebase at /events/{eventId}/divisions/{name}
        /// @todo change name to divisionId
        division.put("name", config.getDivisionId());
        
        // create the file parsers for each type file.
        event = new Event(new File(config.getRootDir(), "divisions.txt"));
        teamList = new TeamList(new File(config.getRootDir(), "teams.txt"));
        matches = new Matches(new File(config.getRootDir(), "matches.txt"));
        
        // create the reports directory which will handle the sub-content in that directory
        reportsDir = new ReportsDir(new File(config.getRootDir(), "reports"));
        
        // see if the event has been created and named yet.
        if(event.getEventName().isEmpty()) {
            // event has not been created yet, create an empty rankings object
            rankings = new Rankings();
        } else {
            // create the rankings object from the correct name of the file
            String name = "reports/Rankings_" + event.getEventName() + ".html";
            rankings = new Rankings(new File(config.getRootDir(), name));
        }
        
        // put the data into the divisions object.
        division.put("teams", teamList.getTeamList());
        division.put("matches", matches.getMatchList());
        division.put("rankings", rankings.getRankingList());
        
        // register the root directory to the directory watcher.  This will cause
        // the dir watcher to get callbacks if any file in the directory changes
        dirWatcher.registerDirectory(config.getRootDir());
        
        // Create the server with the data we created above.
        server = new Server(config.getServer(), body);
        
    }
    
    @Override
    public void run() {
        // Start the server.  This will cause any changes to but uploaded to the
        // server at a maximum rate of 1Hz.
        server.startThread(1000);
        
        // Start the directory watcher to start getting callbacks for file changes
        dirWatcher.start();
        
        // create the UI
        JFrame frame = new JFrame("FTC Score Watcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Ui ui = new Ui();
        ui.setOpaque(true);
        frame.setContentPane(ui);
        
        
        frame.pack();
        frame.setVisible(true);
        
    }
    
    public void exit() {
        this.log.close();
        System.exit(0);
    }
    
    public void log(String title, String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String str = "";
        str += dtf.format(now) + " - ";
        str += title + " - ";
        str += msg;
        
        String dataShort = str;
        if (str.length() > 147)
            dataShort = str.substring(0, 147) + "...";
        
        log.println(str);
        System.out.println(dataShort);
    }
    
    public static void main(String[] args) throws Exception {
        App a = new App();
        
        javax.swing.SwingUtilities.invokeLater(a);
        
        //a.run();
    }
}
