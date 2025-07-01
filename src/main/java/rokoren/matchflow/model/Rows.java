/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow.model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Rok Koren
 */
public class Rows implements MatchProvider
{
    public static final String KEY_ROWS = "rows";    
    
    private final String matchId;    
    private final List<RowData> rows;

    public Rows(String matchId, int size) 
    {
        this.matchId = matchId;
        rows = new ArrayList<>(size);
    }
    
    @Override
    public String getMatchID() 
    {
        return matchId;
    }    
    
    public void addRow(DataProvider data)
    {
        if(data instanceof SpecifiersProvider specifiers)
        {
            rows.add(new RowDataExt(data.getMatketID(), data.getOutcomeID(), specifiers.getSpecifiers()));
        }
        else
        {
            rows.add(new RowData(data.getMatketID(), data.getOutcomeID()));            
        }
    }    
    
    public List<RowData> getRows()
    {
        return Collections.unmodifiableList(rows);
    }
    
    public JsonObject toJson() 
    {
        JsonArray jsons = new JsonArray();
        for(RowData row : getRows())
        {
            jsons.add(row.toJson());
        }
        return new JsonObject().put(KEY_MATCH_ID, matchId).put(KEY_ROWS, jsons);
    }

    public static Rows fromJson(JsonObject json) 
    {
        JsonArray jsons = json.getJsonArray(KEY_ROWS);
        Rows rows = new Rows(json.getString(KEY_MATCH_ID), jsons.size());
        for(int i=0; i<jsons.size(); i++)
        {
            rows.addRow(RowData.fromJson(jsons.getJsonObject(i)));
        }
        return rows;
    }    
}
