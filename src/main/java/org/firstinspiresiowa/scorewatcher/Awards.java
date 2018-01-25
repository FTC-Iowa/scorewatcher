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

import java.io.File;
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
    
    JSONArray awards;
    
    public Awards() {
        file = null;
        awards = new JSONArray();
    }
    
    public JSONArray getAwardsList () {
        return awards;
    }
    
    public Awards(File _file){
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
