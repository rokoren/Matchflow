/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow.exception;

/**
 *
 * @author Rok Koren
 */
public class ParsingException extends Exception
{
    private final String line;

    public ParsingException(String line, String message) 
    {
        super(message);
        this.line = line;
    }        
}
