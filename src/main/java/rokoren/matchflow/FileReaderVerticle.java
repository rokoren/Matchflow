/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.RecordParser;
import java.util.logging.Logger;

/**
 *
 * @author Rok Koren
 */
public class FileReaderVerticle extends VerticleBase
{
    private static final Logger LOG = Logger.getLogger(FileReaderVerticle.class.getName());    
    
    private final String filePath;

    public FileReaderVerticle(String filePath) 
    {
        this.filePath = filePath;
    }

    @Override
    public Future<?> start() 
    {
        OpenOptions options = new OpenOptions();
        return vertx.fileSystem().open(filePath, options).onComplete(res -> {
            if (res.succeeded()) {
                AsyncFile file = res.result();
                RecordParser parser = RecordParser.newDelimited("\n", file);
                parser.handler(lineBuffer -> {
                    String line = lineBuffer.toString().trim();
                    if (!line.isEmpty())
                    {
                        vertx.eventBus().request(ManagerVerticle.ADDRESS, line)
                                .onComplete(ar -> {
                                    if (ar.succeeded()) {
                                        JsonObject json = (JsonObject) ar.result().body();
                                        //LOG.info("JSON: " + json.encodePrettily());
                                        vertx.eventBus().send(json.getString("matchId"), json);      
                                    }
                                });                                            
                    }
                });                
            } else {
                LOG.warning(res.cause().getMessage());
            }
        });
    }    
}
