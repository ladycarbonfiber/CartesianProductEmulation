package com.lcf.cartesian;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.*;
public class CartesianProductTest extends TestCase
{
    public CartesianProductTest(String name)
    {
        super(name);
    }
    public void testProduct()
    {
        ArrayList<String> x1 = new ArrayList<>(Arrays.asList("a","b"));
        ArrayList<String> y1 = new ArrayList<>(Arrays.asList("x","y"));
        ArrayList<String> y2 = new ArrayList<>(Arrays.asList("x","y","z"));
        assertTrue(CartesianProductUtilities.evaluateProduct(x1, y1).equals(new ArrayList<>(Arrays.asList("ax","ay", "bx", "by"))));
        assertTrue(CartesianProductUtilities.evaluateProduct(x1, y2)
        .equals(new ArrayList<>(Arrays.asList("ax","ay", "az", "bx", "by","bz"))));
    }
    public void testNoNest() throws IllegalInputException
    {
      assertEquals("ax ay bx by",CartesianProductUtilities.evaluateInput("{a,b}{x,y}"));
       // assertEquals("abdehi abdfhi abdghi acdehi acdfhi acdghi",CartesianProductUtilities.evaluateInput("a{b,c}d{e,f,g}hi"));
    }
    public void testNoNestHangingMod() throws IllegalInputException
    {
        assertEquals("abdehi abdfhi abdghi acdehi acdfhi acdghi",CartesianProductUtilities.evaluateInput("a{b,c}d{e,f,g}hi"));
    }
    public void testNest() throws IllegalInputException
    {
         assertEquals("ab acd ace acf",
         CartesianProductUtilities.evaluateInput("a{b,c{d,e,f}}"));
         //assertEquals("abijk abijl acdgijk acdgijl acegijk acegijl acfgijk acfgijl ahijk ahijl",
         //CartesianProductUtilities.evaluateInput("a{b,c{d,e,f}g,h}ij{k,l}"));
    }
    public void testNestMultiple() throws IllegalInputException
    {
         assertEquals("abijk abijl acdgijk acdgijl acegijk acegijl acfgijk acfgijl ahijk ahijl",
         CartesianProductUtilities.evaluateInput("a{b,c{d,e,f}g,h}ij{k,l}"));
    }
}