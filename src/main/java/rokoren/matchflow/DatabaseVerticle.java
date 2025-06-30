/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.logging.Logger;
import rokoren.matchflow.model.Row;

/**
 *
 * @author Rok Koren
 */
public class DatabaseVerticle extends VerticleBase
{
    public static final String ADDRESS = "database.manager";    
    
    private static final Logger LOG = Logger.getLogger(DatabaseVerticle.class.getName());    
    
    private Pool pool;
    
    @Override
    public Future<?> start() 
    {
        JDBCConnectOptions connectOptions = new JDBCConnectOptions()
          .setJdbcUrl("jdbc:h2:~/test")
          .setUser("sa")
          .setPassword("");
        PoolOptions poolOptions = new PoolOptions()
          .setMaxSize(16);
        pool = JDBCPool.pool(vertx, connectOptions, poolOptions);   

        try
        {
            String schemaSql = Files.readString(Paths.get("src/main/resources/schema.sql"));
            
            pool
              .query(schemaSql)
              .execute()
              .onFailure(e -> {
                LOG.warning(e.getMessage());
              })
              .onSuccess(rows -> {
                    LOG.info("Creating Database Table Succeeded");
                });                   
        }
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
        }
        
        return vertx.eventBus().consumer(ADDRESS, message -> {
            JsonObject json = (JsonObject)message.body();
            Row row = Row.fromJson(json);
            insert(row);
        }).completion();         
    } 

    private void insert(Row row) 
    {
        Tuple tuple = Tuple.of(
                        row.matchId(),
                        row.marketId(),
                        row.outcomeId(),
                        row.specifiers(),
                        Instant.now());
        
        pool.getConnection()
            .compose(conn -> conn
            .preparedQuery("INSERT INTO match_data (match_id, market_id, outcome_id, specifiers, date_insert) VALUES (?, ?, ?, ?, ?)")
            .execute(tuple)
            // very important! don't forget to return the connection
            .eventually(conn::close))
            .onSuccess(rows -> {
                LOG.info("Data insert in DB success");
            })
            .onFailure(e -> {
                LOG.warning(e.getMessage());
            });      
    }   
}
