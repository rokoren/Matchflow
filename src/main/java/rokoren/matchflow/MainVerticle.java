package rokoren.matchflow;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.ThreadingModel;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

public class MainVerticle extends VerticleBase 
{    
    private static final Logger LOG = Logger.getLogger(MainVerticle.class.getName());    
    
    @Override
    public Future<?> start() 
    {                        
        JDBCConnectOptions connectOptions = new JDBCConnectOptions()
          .setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false") // jdbc:h2:~/test
          .setUser("sa")
          .setPassword("");
        PoolOptions poolOptions = new PoolOptions().setMaxSize(16);
        Pool pool = JDBCPool.pool(vertx, connectOptions, poolOptions);   

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
                    vertx.deployVerticle(new DatabaseVerticle(pool), new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER));
                });                   
        }
        catch(IOException e)
        {
            LOG.warning(e.getMessage());
        }        
                        
        return vertx.deployVerticle(new ManagerVerticle(), new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER).setInstances(1));    
    }
    
    public static void main(String[] args) 
    {
        Map<String, String> arguments = parseArgs(args); 
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle())
                .onComplete(res -> {
                    if (res.succeeded()) {
                        LOG.info("MainVerticle deployed: " + res.result());
                        vertx.deployVerticle(new FileReaderVerticle(arguments.get("filePath")));
                    } else {
                        LOG.warning("Deployment failed!");
                    }
                });     
    }   
    
    private static Map<String, String> parseArgs(String[] args) 
    {
        return Arrays.stream(args)
                .filter(arg -> arg.startsWith("--") && arg.contains("="))
                .map(arg -> arg.substring(2).split("=", 2))
                .collect(java.util.stream.Collectors.toMap(a -> a[0], a -> a[1]));
    }     
}
