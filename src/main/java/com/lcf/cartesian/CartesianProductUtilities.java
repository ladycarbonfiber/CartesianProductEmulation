package com.lcf.cartesian;
import java.util.List;
import java.util.Deque;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Hashtable;
import java.util.regex.*;
public class CartesianProductUtilities
{
    private static Pattern innerSetPattern = Pattern.compile("\\{([^\\{\\}]*?,[^\\{\\}]*?)\\}");
    private static final String delimMarker = "|"; //used to handle nested brackets
    private static final Pattern delimPattern = Pattern.compile("\\|(-?\\d+)\\|");
    private static final Pattern topLevelPattern = Pattern.compile("([\\w\\d]+\\|\\d+\\|)");
    //helper methods for evaluting product

    //called from main class
    public static String evaluateInput(String userInput)throws IllegalInputException
    {
        String rawInput = userInput;
        if(!isVaild(rawInput))
            throw new IllegalInputException("bad input | and ` aren't supported");
        List<String> outputSets = stringProcessing(userInput);
        String out = outPutFormmating(outputSets);
        return out;
    }
    //Evalutes the Cartesian Product of 2 collections, kept as list for simplicty, could be expanded to any iterable of stringable elements
    public static <T> List<String> evaluateProduct(List<T> groupX, List<T> groupY)
    {
       List<String> cartesianProduct = new ArrayList<>();
        for(T elementX : groupX)
        {
            for(T elementY : groupY)
            {
                cartesianProduct.add(elementX.toString()+elementY.toString());//bit hoky for actual strings, but in the spirit of generics
            }
        }
        return cartesianProduct;   
    }
    //takes list of strings and generates output string
    //matches bash style of: x1 x2 ... xn
    private static String outPutFormmating(List<String> in)
    {
        String out = "";
        for(String element : in)
        {
            out += element + " ";
        }
        return out.substring(0,out.length()-1);//remove that trailing space
    }
   private static boolean isVaild(String in)
   {
       if(in.contains("|") || in.contains("`"))
            return false;
        return true;
   }
    //breaks our inner strings into lists to be fed to the product methods
    //token will be ,
    private static List<String> tokenizer(String in, String token)
    {
        String[] elements = in.split(token);
        ArrayList<String> out = new ArrayList<>();
        for(int i =0; i < elements.length; i++)
        {
            out.add(elements[i].trim());//remove any extra white space
        }
        return out;
    }
    //returns indexs that need to be expanded
    private static List<Integer> needsExpansion(List<String> in)
    {
        List<Integer> expandIndexes = new ArrayList<>();
        for(int i = 0; i < in.size(); i++)
        {
            String element = in.get(i);
            if(element.matches(".*\\|-?\\d+\\|.*"))
            {
                   expandIndexes.add(i);
            }
        }
        return expandIndexes;
    }
    //goes from right to left evaluating innermost sets until finished
    private static List<String> stringProcessing(String rawInput)
    {
        Map<Integer,List<String>> nestingMapping = new Hashtable<>();//we use this to map sets to their position left to right
        List<String> dummyList = new ArrayList<>();
        dummyList.add("");
        nestingMapping.put(-1, dummyList);//handles any fake expansions
        int mapCount = 0;
        Matcher m = innerSetPattern.matcher(rawInput);
        while(m.find())
        {
            for(int i =1; i<=m.groupCount(); i++)
            {
                String curGrp = m.group(i);
                nestingMapping.put(mapCount, tokenizer(curGrp,","));
                //because z{set}b needs to map to z, delim, b
                //but {z{set}} needs to map to {z, delim} (hanging commas count as an empty element in the set)
                String delim = "" + delimMarker + mapCount + delimMarker;//We'll use the regex |\d+| later'
                rawInput = rawInput.replace("{"+m.group(i)+"}", delim);
                //System.out.println("currentInput " + rawInput);
                mapCount++;
            }
            m = innerSetPattern.matcher(rawInput);
        }
        
        Deque<List<String>> evalDeque = new ArrayDeque<>();
        List<String> topLevel = new ArrayList<>();
       
        m = topLevelPattern.matcher(rawInput);
        while(m.find())
        {
            //System.out.println(m.group(1));
            List<String> singleTmp = new ArrayList<>();
            singleTmp.add(m.group(1));
            if(!singleTmp.isEmpty())
                evalDeque.addLast(singleTmp);
           
            rawInput = rawInput.replace(m.group(1), "");
           
             m = topLevelPattern.matcher(rawInput);
        }
        List<String> postFix = new ArrayList<>();
       // evalDeque.addFirst(topLevel);
        if(!rawInput.isEmpty())
        {
            if(!rawInput.contains("|"))
                postFix.add(rawInput+"|-1|");
            else
                postFix.add(rawInput);
            List<String> finalElement = postFix;
           
            evalDeque.addLast(finalElement);
        }
        //System.out.println(evalDeque);
        boolean dQHandiness = (evalDeque.size() %2) ==0;//top level arguement evenness determines order of multiplaction given operation is noncommunicative
        List<String> out = new ArrayList<>();
        while(evalDeque.peekFirst() != null)
        {
            
            List<String> currentSet = evalDeque.removeFirst();
            //System.out.println("currSEt is " + currentSet);
            List<Integer> eIndexes = needsExpansion(currentSet);
            List<String> expandedCurrent = new ArrayList<>();
            if(eIndexes.size() != 0)
            {
                for(int i =0; i< currentSet.size(); i++)
                {
                    String activeElement = currentSet.get(i);
                      //System.out.println("activeElement " + activeElement);
                    if(eIndexes.contains(i))
                    {
                        Matcher match = delimPattern.matcher(activeElement);
                        int expansionTarget = -1;
                        if(match.find())
                            expansionTarget = Integer.parseInt(match.group(match.groupCount()));
                        List<String> expanded = nestingMapping.get(expansionTarget);
                        String[] preAndPostDel = activeElement.split("\\|" + expansionTarget+"\\|");
                        if(preAndPostDel.length !=0)
                        {
                            List<String> preTmp = new ArrayList<>();
                            List<String> postTmp = new ArrayList<>();

                            preTmp.add(preAndPostDel[0].trim());
                            if(preAndPostDel.length ==2)
                            {
                                postTmp.add(preAndPostDel[1].trim());
                            }
                            List<String> tempEval = expanded;
                            if(!preTmp.get(0).isEmpty() && !tempEval.isEmpty()&& !preTmp.get(0).contains(delimMarker))
                            {
                                tempEval = evaluateProduct(preTmp,tempEval);
                            }
                            if(!postTmp.isEmpty() )
                            {
                                tempEval = evaluateProduct(tempEval, postTmp);  
                            }
                            expandedCurrent.addAll(tempEval);
                        }
                        else
                        {
                            expandedCurrent.addAll(expanded);
                        }
                    }
                    else
                    {
                        expandedCurrent.add(activeElement);
                    }
                }
                 evalDeque.addLast(expandedCurrent);
            }
            else if(!currentSet.isEmpty())
            {
                out = (out.isEmpty()) ? currentSet :
                (dQHandiness) ? evaluateProduct(currentSet, out):
                 evaluateProduct(out, currentSet);
            }
           
            //System.out.println("Out is " + out);
        }
         return out;
    }
}