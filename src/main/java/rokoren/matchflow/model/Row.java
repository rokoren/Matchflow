/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package rokoren.matchflow.model;

import io.vertx.core.json.JsonObject;
import rokoren.matchflow.exception.NotNumberException;
import rokoren.matchflow.exception.ParsingException;
import rokoren.matchflow.exception.TooFewColumnsException;

/**
 *
 * @author Rok Koren
 */
public class Row extends RowData implements MatchProvider
{    
    private final String matchID;
    
    public Row(String matchID, int marketID, String outcomeID)
    {
        super(marketID, outcomeID);
        this.matchID = matchID;
    }
    
    @Override
    public String getMatchID()
    {
        return matchID;
    }    
    
    public static Row fromLine(String line) throws ParsingException
    {
        try 
        {
            String[] parts = line.split("\\|", -1); // -1 ohrani prazne zadnje stolpce

            if (parts.length < 3) 
            {
                throw new TooFewColumnsException(line);
            }

            String matchId = unquote(parts[0].trim());         // 'sr:match:13762991'
            int marketId = Integer.parseInt(parts[1].trim());   // 60
            String outcomeId = unquote(parts[2].trim());        // '2'
            String specifiers = parts.length > 3 ? unquote(parts[3].trim()) : null; // null ali string

            if(specifiers == null || specifiers.isBlank())
            {
                return new Row(matchId, marketId, outcomeId);
            }
            
            return new RowExt(matchId, marketId, outcomeId, specifiers);

        } 
        catch (NumberFormatException e) 
        {
            throw new NotNumberException(line);
        }
        catch (Exception e) 
        {
            throw new ParsingException(line, e.getMessage());
        }        
    }

    private static String unquote(String input) {
        if (input.startsWith("'") && input.endsWith("'")) {
            return input.substring(1, input.length() - 1);
        }
        return input;
    }

    @Override
    public JsonObject toJson() 
    {
        return super.toJson().put(KEY_MATCH_ID, matchID);
    }
}
