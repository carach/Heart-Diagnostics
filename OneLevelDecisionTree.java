


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Cara
 */
public class OneLevelDecisionTree {
    
    int attr;
    boolean left;
    boolean right;
    public OneLevelDecisionTree(){}
    public OneLevelDecisionTree(int attr, boolean output)
    { 
        this.attr = attr;
        this.left = output;
        this.right = !output;   
    }
    public Boolean getOutcome(DataPoint<Boolean> dp)
    {
        if (dp == null)
            return null;
        if (dp.x[attr] == false)
            return left;
        else
            return right;
    }     
}
