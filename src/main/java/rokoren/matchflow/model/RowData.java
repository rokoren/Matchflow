/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow.model;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author Rok Koren
 */
public class RowData implements DataProvider
{    
    private final String outcomeID;  
    
    private final int marketID;
    
    public RowData(int marketID, String outcomeID)
    {
        this.marketID = marketID;
        this.outcomeID = outcomeID;
    }  
    
    @Override
    public int getMatketID() 
    {
        return marketID;
    }

    @Override
    public String getOutcomeID() 
    {
        return outcomeID;
    }    
    
    public JsonObject toJson() 
    {
        return new JsonObject().put(KEY_MARKET_ID, marketID).put(KEY_OUTCOME_ID, outcomeID);
    }  
    
    public static RowData fromJson(JsonObject json) 
    {
        int marketID = json.getInteger(KEY_MARKET_ID);
        String outcomeID = json.getString(KEY_OUTCOME_ID);
        boolean isMatchProvider = json.containsKey(MatchProvider.KEY_MATCH_ID);
        boolean isSpecifiersProvider = json.containsKey(SpecifiersProvider.KEY_SPECIFIERS);
        if(isMatchProvider)
        {
            if(isSpecifiersProvider)
            {
                return new RowExt(json.getString(MatchProvider.KEY_MATCH_ID), marketID, outcomeID, json.getString(SpecifiersProvider.KEY_SPECIFIERS));
            }
            return new Row(json.getString(MatchProvider.KEY_MATCH_ID), marketID, outcomeID);
        }
        if(isSpecifiersProvider)
        {
            return new RowDataExt(marketID, outcomeID, json.getString(SpecifiersProvider.KEY_SPECIFIERS));
        }        
        return new RowData(marketID, outcomeID);
    }    
}
