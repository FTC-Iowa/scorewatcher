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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jeram
 */
public class ReportsDir implements FileEvents {
    File file;
    
    public ReportsDir(File _file) {
        file = _file;
        if(file.exists()) {
            try {
                App.app.dirWatcher.registerDirectory(file);
            } catch (IOException ex) {
                Logger.getLogger(ReportsDir.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            App.app.dirWatcher.registerFile(this);
        } catch (IOException ex) {
            Logger.getLogger(ReportsDir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void onFileCreate() {
        try {
            App.app.log("ReportsDir", "Reports directory created");
            App.app.dirWatcher.registerDirectory(file);
        } catch (IOException ex) {
            Logger.getLogger(ReportsDir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onFileDelete() {
        
    }

    @Override
    public void onFileModify() {
        
    }
    
}
