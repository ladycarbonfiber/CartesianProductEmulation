package com.lcf.cartesian;
//Allows for graceful failure uppon encountering illegal input
// | and ` are reserved characters in bash so unescaped input is illegal 

public class IllegalInputException extends Exception
{
    public IllegalInputException(String message)
    {
        super(message);
    }
}