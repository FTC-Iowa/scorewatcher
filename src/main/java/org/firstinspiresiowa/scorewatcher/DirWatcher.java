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
import java.nio.file.FileSystems;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vens
 */
public class DirWatcher extends Thread{
    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final ArrayList<Integer> dirHashes;
    
    /// Map of file name to the callbacks to call when the file changes
    private final Map<String, FileEvents> fileCallbacks;
    
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    
    /**
     * Register a directory to be watched.
     * This will cause the passed in directory to be watched.  Any time a file changes
     * in this directory the DirWatcher will check to see if that file has been registered
     * with registerFile() and if so it will call the callbacks on that file.
     * @param directory Directory to watch
     * @throws IOException  If the directory param is not actually a directory or doesn't exist
     * @todo make this private
     */
    public void registerDirectory(File directory) throws IOException {
        File dir = directory;
        App.app.log("DirWatcher", "Watching for changes in files in the directory " + dir.toPath());
        WatchKey key = dir.toPath().register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir.toPath());
        dirHashes.add(dir.hashCode());
    }
    
    /**
     * Register a file to the DirWatcher.
     * If this file is in a directory that registerDirectory() was called on and
     * the file changes then the callbacks will be called on this file.
     * @param file The file to watch.
     */
    public void registerFile(FileEvents file) throws IOException {
        App.app.log("DirWatcher", "Watching for changed in the file " + file.getFile().getAbsolutePath());
        fileCallbacks.put(file.getFile().getAbsolutePath(), file);
        if(!dirHashes.contains(file.getFile().getParentFile().hashCode())) {
            registerDirectory(file.getFile().getParentFile());
        }
    }
    
    /**
     * Create a DirWatcher.  By default this will not be watching any directories
     * @throws IOException 
     */
    public DirWatcher () throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.dirHashes = new ArrayList<>();
        this.fileCallbacks = new HashMap<>();
    }
    
    @Override
    /**
     * This will run the DirWatcher thread.  While running it will watch for any
     * changes made to the registered files, and if any events occur it will
     * call the callbacks.
     */
    public void run() {
        for (;;) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            
            Path dir = keys.get(key);
            if (dir == null) {
                App.app.log("DirWatcher", "WatchKey not recognized!!");
                continue;
            }
            
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                
                if (kind == OVERFLOW) {
                    continue;
                }
                
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                
                // print out event
                //System.out.format("%s: %s\n", event.kind().name(), name.toAbsolutePath());
                
                App.app.log("DirWatcher", "File Changed: " + child.toAbsolutePath().toString());
                FileEvents file = fileCallbacks.get(child.toAbsolutePath().toString());
                if ( file != null ) {
                    //DirectoryEvents d = callbacks.get(key);
                    if (kind == ENTRY_CREATE) {
                        file.onFileCreate();
                    } else if (kind == ENTRY_DELETE) {
                        file.onFileDelete();
                    } else if (kind == ENTRY_MODIFY) {
                        file.onFileModify();
                    } else {
                        App.app.log("DirWatcher", "Unknown event type: " + kind.name());
                    }
                }
            }
            
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if(keys.isEmpty()) {
                    break;
                }
            }
        }
        App.app.log("DirWatcher", "The DirWatcher has stopped");
    }
}