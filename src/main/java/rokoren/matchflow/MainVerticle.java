package rokoren.matchflow;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.ThreadingModel;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

public class MainVerticle extends VerticleBase 
{
    public static final String PROP_FILE_PATH = "filePath";
    
    private static final Logger LOG = Logger.getLogger(MainVerticle.class.getName());    
    
    @Override
    public Future<?> start() 
    {
        String filePath = config().getString(PROP_FILE_PATH);
        DeploymentOptions options = new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER);
        vertx.deployVerticle(new DatabaseVerticle(), options)
                .onComplete(res1 -> {
                    if (res1.succeeded()) {
                        LOG.info("DatabaseVerticle deployed: " + res1.result());
                        
                        vertx.deployVerticle(new ManagerVerticle(), options)
                                .onComplete(res2 -> {
                                    if (res2.succeeded()) {
                                        LOG.info("ManagerVerticle deployed: " + res2.result());
                                        vertx.deployVerticle(new FileReaderVerticle(filePath));
                                    } else {
                                        LOG.warning("Deployment failed!");
                                    }
                                });                          
                    } else {
                        LOG.warning("Deployment failed!");
                    }
                });  
            return Future.succeededFuture();
    }
    
    public static void main(String[] args) 
    {
        Map<String, String> arguments = parseArgs(args); 
        
        JsonObject config = new JsonObject().put(PROP_FILE_PATH, arguments.get(PROP_FILE_PATH));
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle(), options)
                .onComplete(res -> {
                    if (res.succeeded()) {
                        LOG.info("MainVerticle deployed: " + res.result());
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
