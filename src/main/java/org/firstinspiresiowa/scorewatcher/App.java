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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.json.simple.JSONObject;

/**
 *
 * @author jeram
 */
public final class App implements Runnable{
    public final JSONObject body;
    public final JSONObject data;
    public final Config config;
    public final Server server;
    public final TeamList teamList;
    public final DirWatcher dirWatcher;
    public final Awards awards;
    public final Matches matches;
    public final Rankings rankings;
    public final Event event;
    public final ReportsDir reportsDir;
    private final PrintWriter log;
    
    public static App app;
    public App() throws FileNotFoundException, IOException {
        app = this;
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        
        this.log = new PrintWriter("firstinspiresiowa_" + dtf.format(now) + ".log", "UTF-8");
        
        
        
        
        
        try {
            config = new Config();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            log("init", "Could not open config");
            throw ex;
        }
        
        dirWatcher = new DirWatcher();
        
        body = new JSONObject();
        data = new JSONObject();
        body.put("data", data);
        body.put("eventId", config.getEventId());
        body.put("divisionId", config.getDivisionId());
        body.put("passphrase", config.getPassphrase());
        
        
        data.put("name", config.getDivisionId());
        
        event = new Event(new File(config.getRootDir(), "divisions.txt"));
        teamList = new TeamList(new File(config.getRootDir(), "teams.txt"));
        matches = new Matches(new File(config.getRootDir(), "matches.txt"));
        awards = new Awards(new File(config.getRootDir(), "awards.txt"));
        
        reportsDir = new ReportsDir(new File(config.getRootDir(), "reports"));
        
        
        if(event.getEventName().isEmpty()) {
            rankings = new Rankings();
        } else {
            String name = "reports/Rankings_" + event.getEventName() + ".html";
            rankings = new Rankings(new File(config.getRootDir(), name));
        }
        
        
        data.put("teams", teamList.getTeamList());
        data.put("matches", matches.getMatchList());
        data.put("awards", awards.getAwardsList());
        data.put("rankings", rankings.getRankingList());
        
        
        dirWatcher.registerDirectory(config.getRootDir());
        
        //System.out.println(body.toJSONString());
        
        server = new Server(config.getServer(), body);
        
    }
    
    public void run() {
        //System.out.println("hello world");
        server.startThread(1000);
        dirWatcher.start();
        
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
