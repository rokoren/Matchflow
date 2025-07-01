/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import rokoren.matchflow.model.MatchProvider;
import rokoren.matchflow.model.RowData;
import rokoren.matchflow.model.Rows;
import rokoren.matchflow.model.SpecifiersProvider;

/**
 *
 * @author Rok Koren
 */
public class DatabaseVerticle extends VerticleBase
{
    public static final String ADDRESS_INSERT = "database.insert";
    
    public static final int FAILURE_CODE = 100;
    
    private static final Logger LOG = Logger.getLogger(DatabaseVerticle.class.getName());    
    
    private final Pool pool;
    
    private final long maxFlushDelayMs = 5000; 
    private long lastFlushTime = System.currentTimeMillis();     

    public DatabaseVerticle(Pool pool) {
        this.pool = pool;
    }        
    
    @Override
    public Future<?> start() 
    {  
        vertx.setPeriodic(1000, timerId -> {
            long now = System.currentTimeMillis();

            // Print min, max times če je dosežen maxFlushDelay
            if ((now - lastFlushTime) >= maxFlushDelayMs) 
            {
                lastFlushTime = now; 
                printMinMaxInsertTimes();
            }
        });         
        
        return vertx.eventBus().consumer(ADDRESS_INSERT, message -> {
            JsonObject json = (JsonObject)message.body();
            Rows rows = Rows.fromJson(json);
            
            Instant insertTime = Instant.now();
            List<Tuple> batch = new ArrayList(rows.getRows().size());
            for(RowData row : rows.getRows())
            {
                batch.add(getTuple(rows.getMatchID(), row, insertTime));
            }

            pool.getConnection()
                .compose(conn -> conn
                .preparedQuery("INSERT INTO match_data (match_id, market_id, outcome_id, specifiers, date_insert) VALUES (?, ?, ?, ?, ?)")
                .executeBatch(batch)
                // very important! don't forget to return the connection
                .eventually(conn::close))
                .onSuccess(commit -> {
                    //LOG.info("Batch insert succeeded for match ID: " + rows.getMatchID());
                    lastFlushTime = System.currentTimeMillis(); 
                    message.reply(new JsonObject().put(MatchProvider.KEY_MATCH_ID, rows.getMatchID()).put(Rows.KEY_ROWS, commit.rowCount()));
                })
                .onFailure(e -> {
                    LOG.warning(e.getMessage());
                    message.fail(FAILURE_CODE, e.getMessage());
                });             
        }).completion();         
    }   
    
    private Tuple getTuple(String matchID, RowData data, Instant insertTime)
    {
        String specifiers = null;
        int marketID = data.getMatketID();
        String outcomeID = data.getOutcomeID();
        if(data instanceof SpecifiersProvider provider)
        {
            specifiers = provider.getSpecifiers();
        }
        
        return Tuple.of(matchID, marketID, outcomeID, specifiers, insertTime);       
    }
    
    private void printMinMaxInsertTimes() 
    {
        pool.getConnection()
            .compose(conn -> conn
            .query("SELECT MIN(date_insert) AS min_time, MAX(date_insert) AS max_time FROM match_data")
            .execute()
            .onSuccess(rows -> {
                RowSet<Row> result = rows;
                if (result.iterator().hasNext()) {
                    Row row = result.iterator().next();
                    LOG.info("🟢 Min date_insert: " + row.getLocalDateTime("min_time"));
                    LOG.info("🔵 Max date_insert: " + row.getLocalDateTime("max_time"));
                } else {
                    LOG.info("⚠️ No data found in match_data table.");
                }
            })
            .onFailure(err -> {
                LOG.warning("❌ Failed to fetch min/max insert dates: " + err.getMessage());
            }));        
    } 
}
