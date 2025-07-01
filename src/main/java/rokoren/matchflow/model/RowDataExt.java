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
public class RowDataExt extends RowData implements SpecifiersProvider
{
    private final String specifiers;

    public RowDataExt(int marketID, String outcomeID, String specifiers) 
    {
        super(marketID, outcomeID);
        this.specifiers = specifiers;
    }  
    
    @Override
    public String getSpecifiers() 
    {
        return specifiers;
    }       
    
    @Override
    public JsonObject toJson() 
    {
        return super.toJson().put(KEY_SPECIFIERS, specifiers);
    }      
}
