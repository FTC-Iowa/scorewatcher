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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author jeram
 */
public class Rankings implements FileEvents{
    private File file;
    
    JSONArray rankings;
    
    public Rankings() {
        file = null;
        rankings = new JSONArray();
    }
    
    public JSONArray getRankingList () {
        return rankings;
    }
    
    public Rankings(File _file){
        this();
        file = _file;
        if(file.exists()) {
            parseFile();
        }
        App.app.dirWatcher.registerFile(this);
    }
    
    public void setFile(File _file) {
        file = _file;
        if(file.exists()) {
            parseFile();
        }
        App.app.dirWatcher.registerFile(this);
    }
    
    private void parseFile() {
        Element table;
        try {
            table = this.getHtmlTable();
        } catch (Exception ex) {
            return;
        }
        
        Elements rows = table.getElementsByTag("tr");
        boolean isLeague;
        isLeague = "LEAGUE_CHAMPIONSHIP".equals(App.app.event.getEventType());
        
        int i = isLeague ? 2 : 1;
        
        if(rows.size() <= i)
            return;
        
        int j = 0;
        for(; i< rows.size(); i++) {
            Element row = rows.get(i);
            try {
                JSONObject rank = parseRow(row);
                if( j < rankings.size()) {
                    rankings.set(j, rank);
                } else {
                    rankings.add(j, rank);
                }
                j++;
            } catch (Exception e) {
                
            }
        }
        
        App.app.log("Ranking List", rankings.toJSONString());
    }
    
    private JSONObject parseRow(Element row) {
        JSONObject json = new JSONObject();
        Elements cols = row.getElementsByTag("td");
        
        json.put("rank", Integer.parseInt(cols.get(0).text()));
        json.put("team", Integer.parseInt(cols.get(1).text()));
        json.put("qp", Integer.parseInt(cols.get(3).text()));
        json.put("rp", Integer.parseInt(cols.get(4).text()));
        json.put("highest", Integer.parseInt(cols.get(5).text()));
        json.put("matches", Integer.parseInt(cols.get(6).text()));
        if (cols.size() > 7) {
            JSONObject league = new JSONObject();
            league.put("qp", Integer.parseInt(cols.get(7).text()));
            league.put("rp", Integer.parseInt(cols.get(8).text()));
            league.put("matches", Integer.parseInt(cols.get(9).text()));
            json.put("league", league);
        }
        
        App.app.log("Parsed Ranking", json.toJSONString());
        return json;
    }
    
    private Element getHtmlTable() throws Exception{
        Document doc = Jsoup.parse(file, "UTF-8", "");
        Elements tables = doc.getElementsByTag("table");
        if (tables.isEmpty()) {
            System.err.println("Could not find table in HTML document");
            throw new Exception();
        }
        return tables.first();
    }
    
    private JSONArray parseHtmlFile() {
        JSONArray json = new JSONArray();
        
        
        
        return json;
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
