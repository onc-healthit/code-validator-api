package org.sitenv.vocabularies.watchdog;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.engine.ValidationEngine;


public class RepositoryWatchdog  implements Runnable {

	private static Logger logger = Logger.getLogger(RepositoryWatchdog.class);
	
	private boolean stopped = false;
	private boolean updateRequired = false;
	
	private Timer timer;
	private Thread thread;
	
	private final WatchService watchService;
    private final Map<WatchKey,Path> pathMap;
    private final Boolean recursive;
    private final String rootDirectory;
   
	
    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        
            Path prev = pathMap.get(key);
            if (prev == null) {
                // TODO: log4j
            	logger.debug("Registering watch event: " + dir);
            } else {
                if (!dir.equals(prev)) {
                	// TODO: log4j
                    System.out.format("Updating watch event: " + prev + " -> " + dir);
                }
            }
       
        pathMap.put(key, dir);
    }

   
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    public RepositoryWatchdog(String directory, boolean recursive) throws IOException {
    	// register directory and process its events
    	Path dir = Paths.get(directory);
    	this.watchService = FileSystems.getDefault().newWatchService();
        this.pathMap = new HashMap<WatchKey,Path>();
        this.recursive = recursive;
        this.rootDirectory = directory;

        if (recursive) {
        	// TODO: log4j
            logger.debug("Scanning " + dir + "...");
            registerAll(dir);
            // TODO: log4j
            logger.debug("Done.");
        } else {
            register(dir);
        }

    }
    
    public synchronized void start() {
        stopped = false;
        thread = new Thread(this, "REPOSITORY-WATCHDOG");
        //t.setDaemon(true);
        thread.start();
    }
    
    public synchronized void stop() {
    	try {
    		this.stopped = true;
    		this.watchService.close();
    	}
    	catch (Exception e) 
    	{
    		logger.error("Exception while stopping watchService", e);
    	}
    }
    
	public void run() {
		int count = 0;
		while (!stopped) {

            // wait for key to be signaled
            WatchKey key;
            try {
            	logger.debug("Polling for service events");
                key = watchService.take();
            } catch (InterruptedException x) {
            	logger.error("Interrupting the watchService...");
                return;
            } catch (ClosedWatchServiceException x) {
            	logger.error("Closing the watch service...");
                return;
            }
            
            if (key == null)
            {
            	continue;
            }
            
            Path dir = pathMap.get(key);
            if (dir == null) {
            	// TODO: log4j
                logger.error("WatchKey not recognized!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                } else {
                	updateRequired = true;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                
                // TODO: log4j
              	logger.debug(count++ + " - " + event.kind().name() + ": " + child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == StandardWatchEventKinds.ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readable
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                pathMap.remove(key);

                // all directories are inaccessible
                if (pathMap.isEmpty()) {
                    break;
                }
            }
            if (updateRequired)
            {
            	TimerTask timerTask = new TimerTask() {
            		
            		@Override
                    public void run() {
            			logger.debug("Updating Repository");
            			timer.cancel();
            			timer = null;
            			
            			try {
            				ValidationEngine.loadDirectory(rootDirectory);
            			} catch (IOException e) {
            				// TODO: log4j
            				logger.error("Error performing a load of the directory.", e);
            			}
                    }
            	};
            	
            	if (timer != null)
            	{
            		timer.cancel();
            	}
            	timer = new Timer();
            	timer.schedule(timerTask, 10000);
            	
            	updateRequired = false;
            	
            	logger.debug("Not updating repository.");

            }
        }
		
	}


	
}
