/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import rokoren.matchflow.model.RowData;
import rokoren.matchflow.model.Rows;

/**
 *
 * @author Rok Koren
 */
public class MatchVerticle extends VerticleBase
{    
    private static final Logger LOG = Logger.getLogger(MainVerticle.class.getName());     
    
    private final Queue<RowData> queue = new ConcurrentLinkedQueue<>();
    private final int maxBatchSize = 100;
    private final long maxFlushDelayMs = 5000;    
    private final String matchID;
    
    private long lastFlushTime = System.currentTimeMillis();    

    public MatchVerticle(String matchID) 
    {
        this.matchID = matchID;
    }

    @Override
    public Future<?> start() 
    {
        vertx.setPeriodic(1000, timerId -> {
            long now = System.currentTimeMillis();

            // Flush če je dosežen maxBatchSize ali maxFlushDelay
            if (queue.size() >= maxBatchSize || (now - lastFlushTime) >= maxFlushDelayMs) 
            {
                Rows rows = new Rows(matchID, maxBatchSize);
                
                RowData data;
                while ((data = queue.poll()) != null) {
                    rows.addRow(data);
                }

                if(!rows.getRows().isEmpty())
                {
                    lastFlushTime = now; 
                    vertx.eventBus().request(DatabaseVerticle.ADDRESS_INSERT, rows.toJson())
                            .onComplete(ar -> {
                                if (ar.succeeded()) {
                                    JsonObject json = (JsonObject) ar.result().body();
                                    //LOG.info("JSON: " + json.encodePrettily());    
                                }
                            });                     
                } 
            }
        });      
        
        return vertx.eventBus().consumer(matchID, message -> {
            //LOG.info("MatchVerticle: " + message.body()); 
            JsonObject json = (JsonObject) message.body();
            queue.offer(RowData.fromJson(json));
        }).completion(); 
    }     
}
