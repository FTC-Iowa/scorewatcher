/*
 * Copyright 2018 Jason Cheng.
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
  * @author jason
  */

  public Inspections(File _file) {
      file = _file;
      inspectionArray = new JSONArray();
      if(file.exists()) {
          parseFile();
      }

      try {
          App.app.dirWatcher.registerFile(this);
      } catch (IOException ex) {
          Logger.getLogger(Inspections.class.getName()).log(
            Level.SEVERE, null, ex);
      }
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

          App.app.log("Inspection List", inspectionArray.toJSONString());

      } catch (FileNotFoundException ex) {
          Logger.getLogger(TeamList.class.getName()).log(
            Level.SEVERE, null, ex);
      } catch (IOException ex) {
          Logger.getLogger(TeamList.class.getName()).log(
            Level.SEVERE, null, ex);
      }
  }

  private JSONObject parseRow(Element row) {
      JSONObject json = new JSONObject();
      Elements cols = row.getElementsByTag("td");

      json.put("team", Integer.parseInt(cols.get(0).text()));
      json.put("robot", Integer.parseInt(cols.get(1).text()));
      json.put("field", Integer.parseInt(cols.get(2).text()));
      json.put("judging", Integer.parseInt(cols.get(3).text()));
      json.put("picture", Integer.parseInt(cols.get(4).text()));

      App.app.log("Parsed Inspections", json.toJSONString());
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
