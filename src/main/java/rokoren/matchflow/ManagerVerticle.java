/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import rokoren.matchflow.model.Row;

/**
 *
 * @author Rok Koren
 */
public class ManagerVerticle extends VerticleBase
{
    public static final String ADDRESS = "match.manager";
    
    private static final Logger LOG = Logger.getLogger(MainVerticle.class.getName());   
    
    private final Map<String, Future<String>> verticles = new HashMap<>();
    
    @Override
    public Future<?> start()    
    {        
        return vertx.eventBus().consumer(ADDRESS, message -> {
            //LOG.info("Line: " + message.body());
            String line = message.body().toString();            
            Row row = Row.fromLine(line);
            Future<String> future = verticles.get(row.matchId());
            if(future == null)
            {
                future = vertx.deployVerticle(new MatchVerticle(row.matchId()));
                verticles.put(row.matchId(), future);                  
            }
            future.onComplete(ar -> {
                message.reply(row.toJson());
            });            
        }).completion();        
    }   
}
