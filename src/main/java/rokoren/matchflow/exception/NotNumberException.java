/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rokoren.matchflow.exception;

/**
 *
 * @author Rok Koren
 */
public class NotNumberException extends ParsingException
{
    public NotNumberException(String line) 
    {
        super(line, "Value is not a number");
    }     
}
