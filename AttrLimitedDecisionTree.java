
import java.util.Arrays;



/*
 * train a decision tree only with given number of attributes.
 * no longer heuristic greedy algorithm
 */

/**
 *
 * @author Mousie
 */
public class AttrLimitedDecisionTree {

    node root;
    
    String treetype;
    int[] treeAttr;
    boolean[] treeOutput;
    
    public static class node{
        int attr;
        node left;
        node right;
        boolean output;
    }
    public AttrLimitedDecisionTree(){}
    public AttrLimitedDecisionTree(int[] attrSet,String type,boolean[] outputCombi)
    {
        
        root = new node();
        if ( attrSet == null || type == null || outputCombi == null) {}
        
        root.attr = attrSet[0];
        root.left = new node();
        root.right = new node();
        
        // just for print out;
        treetype = type;
        treeAttr = attrSet;
        treeOutput = outputCombi;
        
        switch(type){
            case "balance": root.left.attr = attrSet[1];
                            root.left.left = new node();
                            root.left.left.output = outputCombi[0];
                            root.left.right = new node();
                            root.left.right.output = outputCombi[1];
                            root.right.attr = attrSet[2];
                            root.right.left = new node();
                            root.right.left.output = outputCombi[2];
                            root.right.right = new node();
                            root.right.right.output = outputCombi[3];
                            break;
            case "leftZigzig": root.left.attr = attrSet[1];
                            root.right.output = outputCombi[0];
                            root.left.left = new node();
                            root.left.left.attr = attrSet[2];
                            root.left.right = new node();
                            root.left.right.output = outputCombi[1];
                            root.left.left.left = new node();
                            root.left.left.left.output = outputCombi[2];
                            root.left.left.right = new node();
                            root.left.left.right.output = outputCombi[3];
                            break;
            case "rightZigzig":root.right.attr = attrSet[1];
                            root.left.output = outputCombi[0];
                            root.right.right = new node();
                            root.right.right.attr = attrSet[2];
                            root.right.left = new node();
                            root.right.left.output = outputCombi[1];
                            root.right.right.right = new node();
                            root.right.right.right.output = outputCombi[2];
                            root.right.right.left = new node();
                            root.right.right.left.output = outputCombi[3];
                            break;
            case "leftZigzag": root.left.attr = attrSet[1];
                            root.right.output = outputCombi[0];
                            root.left.left = new node();
                            root.left.left.output = outputCombi[1];
                            root.left.right = new node();
                            root.left.right.attr = attrSet[2];
                            root.left.right.left = new node();
                            root.left.right.left.output = outputCombi[2];
                            root.left.right.right = new node();
                            root.left.right.right.output = outputCombi[3];
                            break;
            case "rightZigzag": root.right.attr = attrSet[1];
                            root.left.output = outputCombi[0];
                            root.right.right = new node();
                            root.right.right.output = outputCombi[1];
                            root.right.left = new node();
                            root.right.left.attr = attrSet[2];
                            root.right.left.right = new node();
                            root.right.left.right.output = outputCombi[2];
                            root.right.left.left = new node();
                            root.right.left.left.output = outputCombi[3];
                            break;
                       
    }
    }
    
    public Boolean getOutcome(DataPoint<Boolean> dp)
    {
        if (dp == null || root == null)
            return null;
        node curr = this.root;
        while(curr.left != null && curr.right != null)
            if (dp.x[curr.attr])
                curr = curr.right;
            else
                curr = curr.left;
        return curr.output;
    }
public void printTree()
    {
        System.out.println("Attributes: " + Arrays.toString(this.treeAttr)  + " " + "type: " + "Output; " + this.treetype + " " + Arrays.toString(this.treeOutput));
    }
    
}
