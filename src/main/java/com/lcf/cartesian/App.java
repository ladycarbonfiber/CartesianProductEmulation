package com.lcf.cartesian;
import java.util.Scanner;
/**
 * @Author: Tom Wells
 * 
 * Command line utility which emulates the Cartesian product functionality of Bash
 * Cartesian Product defined as the set of all pairs (x,y) for which X belongs to X and y to Y 
 * 
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println("CartesianProduct Demo, sets are delienated with matching {}");
        System.out.println("Write an expression and press enter to begin");
        Scanner inputReader = new Scanner(System.in);
        boolean cont = true;
        while(cont)
        {
            String userInput = inputReader.nextLine();
            try{
                System.out.println( CartesianProductUtilities.evaluateInput(userInput));
            }
            catch(IllegalInputException e){
                System.out.println("Sorry, | and ` are not supported");
            }
            System.out.println("Enter another? \n [Y] to continue");
            cont = inputReader.nextLine().equals("Y");

        }
    }
}
