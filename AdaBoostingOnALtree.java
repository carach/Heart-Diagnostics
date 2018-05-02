
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mousie
 */
public class AdaBoostingOnALtree {
    
    LinkedList<DataPoint<Boolean>> dptraininglst;
    LinkedList<DataPoint<Boolean>> dptestlst;
    
    LinkedList<AttrLimitedDecisionTree> hypothesisSpace;   
    HashMap<DataPoint<Boolean>,Double> weights = new HashMap<>();    
    double epsilon;
    HashMap<AttrLimitedDecisionTree,Double> choosenTrees;

    public AdaBoostingOnALtree(LinkedList<DataPoint<Boolean>> dplstTrain, LinkedList<DataPoint<Boolean>> dplstTest){
        dptraininglst = dplstTrain;
        dptestlst = dplstTest;
        int m = dptraininglst.size();
        for(DataPoint<Boolean>dp : dplstTrain)
            weights.put(dp, (double)1/m);
        choosenTrees = new HashMap<>();
        
        hypothesisSpace = addTreeToHypothesisSpace(find3Attr(22),findAllOutputs());
    }

    
    public static LinkedList<AttrLimitedDecisionTree> addTreeToHypothesisSpace(LinkedList<int[]> attrSetLst,LinkedList<boolean[]> outputLst)
    {
        LinkedList<AttrLimitedDecisionTree> atths = new LinkedList();
        
        LinkedList<String> typelst = new LinkedList();
        typelst.add("balance");
        typelst.add("leftZigzig");
        typelst.add("rightZigzig");
        typelst.add("leftZigzag");
        typelst.add("rightZigzag");
        
        for (int[] a: attrSetLst)
            for (String t: typelst)
                for (boolean[] ot: outputLst)
                {
                    AttrLimitedDecisionTree ad = new AttrLimitedDecisionTree(a,t,ot);
                    atths.add(ad);
                }
        return atths;
    }
    
    public static LinkedList<int[]> find3Attr(int n)
    {
        if ( n < 1)
            return null;
        LinkedList<int[]> lst = new LinkedList<>(); 
        for(int i = 0; i < n; i ++)
            for ( int j = 0; j < n; j ++)
                for ( int k = 0; k < n; k ++)
                {
                    int[]e = {i,j,k};
                    lst.add(e);
                }    
        return lst;
    }
    public static LinkedList<boolean[]> findAllOutputs()
    {
        LinkedList<boolean[]> lst = new LinkedList<>();
        for(int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++)
                    for (int l = 0; l < 2; l++)
                    {
                        boolean[]e = {(i==1),(j==1),(k==1),(l==1)};
                        lst.add(e);
                    }
        return lst;
    }
    
    public void updateWeights (AttrLimitedDecisionTree root)
    {
        if (root == null)
            return;
        
        if (epsilon >=0.5)
        {
            System.out.println("The learner is too weak...");
            return;
        }
        
        double alpha = 0.5 * Math.log((1 - epsilon)/epsilon);

        for (DataPoint <Boolean> dp: weights.keySet())
        {
            double tmp = weights.get(dp);
            
            if (dp.y == root.getOutcome(dp))   // correctly classified
                weights.replace(dp,tmp * Math.pow(Math.E, - alpha) / (2 * Math.pow(epsilon * ( 1 - epsilon ),0.5)));
            else           
                weights.replace(dp,tmp * Math.pow(Math.E, alpha) / (2 * Math.pow(epsilon * ( 1 - epsilon ),0.5)));

        }
    }
    /* select a tree within hypothesis space which has a minimium weighted error.
    * 
    * @param LinkedList<DataPoint<AnyType>>
    * return the root of the select tree
    * also update epsilon
    */
    public AttrLimitedDecisionTree selectTree()
    {
        double minError = 1;
        AttrLimitedDecisionTree choosenTree = new AttrLimitedDecisionTree();
        for (AttrLimitedDecisionTree root: hypothesisSpace)
        {
            double accumError = 0;
            for (DataPoint<Boolean> dp: dptraininglst)
                if (dp.y != root.getOutcome(dp))   // misclassified
                    accumError = accumError + weights.get(dp);
            if (accumError < minError)
            {
                minError = accumError;
                choosenTree = root;
            }
        }
        epsilon = minError;
        
        System.out.println("Tree selected.");
        choosenTree.printTree();
        System.out.println("epsilon for this round: "+ epsilon);
        
        hypothesisSpace.remove(choosenTree);
        
        return choosenTree;
    }
    
    public void boost(int times)
    {
        int counter = 1;
        System.out.println("Round "+ counter);
        while(counter <= times) {
            AttrLimitedDecisionTree workingTree = selectTree();
            choosenTrees.put(workingTree,0.5 * Math.log((1 - epsilon)/epsilon));
            updateWeights(workingTree);

        System.out.println("Accuracy on training set: " + evaluate(dptraininglst));  
        System.out.println("Accuracy on test set: " + evaluate(dptestlst));
        counter ++;
        }
    }
    public boolean getOutcome(DataPoint<Boolean> dp)
    {
        double value = 0;
        for (Entry<AttrLimitedDecisionTree,Double> e: choosenTrees.entrySet())
        {
            if ( e.getKey().getOutcome(dp) == true)
                value = value + e.getValue();
            else
                value = value - e.getValue();
        }
        return value >= 0;
            
    }
    
    public double evaluate(LinkedList<DataPoint<Boolean>> dplst)
    {
        int corr = 0;
        for(DataPoint<Boolean> dp: dplst)
            if(dp.y == getOutcome(dp))
                corr ++;
        return (double)corr/(double)dplst.size();
    }
    
    public void printChoosenTrees(HashMap<AttrLimitedDecisionTree,Double> choosenTrees)
    {
        System.out.println("Classifier: ");
        for (Entry<AttrLimitedDecisionTree,Double> e: choosenTrees.entrySet())
        { 
            System.out.println("Tree attributes: " + Arrays.toString(e.getKey().treeAttr) );
            System.out.println("Tree type: " + e.getKey().treetype);
            System.out.println("Tree outputs: " + Arrays.toString(e.getKey().treeOutput));
            System.out.println("Tree's alpha: " + e.getValue());
        }
    }
}
