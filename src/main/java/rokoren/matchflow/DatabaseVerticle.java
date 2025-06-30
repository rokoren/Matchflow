/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import java.util.logging.Logger;

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
        
        return vertx.eventBus().consumer(ADDRESS, message -> {
            LOG.info("Line: " + message.body());
            /*
            pool.getConnection()
                    .compose(conn -> conn
                    .query("SELECT * FROM user")
                    .execute()
                    // very important! don't forget to return the connection
                    .eventually(conn::close))
                    .onSuccess(rows -> {
                        for (Row row : rows) {
                            System.out.println(row.getString("FIRST_NAME"));
                        }
                    })
                    .onFailure(e -> {
                        // handle the failure
                    });
            */
        }).completion();         
    }     
}
