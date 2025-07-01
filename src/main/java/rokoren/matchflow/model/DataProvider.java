/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rokoren.matchflow.model;

/**
 *
 * @author Rok Koren
 */
public interface DataProvider 
{
    String KEY_MARKET_ID  = "marketId";
    String KEY_OUTCOME_ID = "outcomeId";  

    int getMatketID();
    String getOutcomeID();
}
