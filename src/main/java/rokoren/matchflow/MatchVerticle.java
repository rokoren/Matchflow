/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import java.util.logging.Logger;

/**
 *
 * @author Rok Koren
 */
public class MatchVerticle extends VerticleBase
{    
    private static final Logger LOG = Logger.getLogger(MainVerticle.class.getName());     
    
    private final String matchID;

    public MatchVerticle(String matchID) 
    {
        this.matchID = matchID;
    }

    @Override
    public Future<?> start() 
    {
        return vertx.eventBus().consumer(matchID, message -> {
            //LOG.info("MatchVerticle: " + message.body()); 
            vertx.eventBus().send(DatabaseVerticle.ADDRESS, message.body());
        }).completion(); 
    }    
}
