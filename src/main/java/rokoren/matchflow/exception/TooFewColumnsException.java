/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow.exception;

/**
 *
 * @author Rok Koren
 */
public class TooFewColumnsException extends ParsingException
{
    public TooFewColumnsException(String line) 
    {
        super(line, "Too few columns");
    }    
}
