
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Cara
 */
public class AdaBoostingOnOLtree {
    Dataset dptraininglst;
    Dataset dptestlst;
    
    LinkedList<OneLevelDecisionTree> hypothesisSpace;   
    HashMap<DataPoint<Boolean>,Double> weights;    
    double epsilon;
    HashMap<OneLevelDecisionTree,Double> choosenTrees;
    public AdaBoostingOnOLtree(Dataset dplstTrain, Dataset dplstTest)
    {
        dptraininglst = dplstTrain;
        dptestlst = dplstTest;
        hypothesisSpace = addTreeToHypothesisSpace();
        weights = new HashMap<>();
        int m = dptraininglst.size();
        for(DataPoint<Boolean>dp : dplstTrain)
            weights.put(dp, (double)1/m);
        choosenTrees = new HashMap<>();
    }
    
    public static LinkedList<OneLevelDecisionTree> addTreeToHypothesisSpace()
    {
        LinkedList<OneLevelDecisionTree> atths = new LinkedList<>();
        for( int i = 0; i < 22; i ++)
        {
            OneLevelDecisionTree t = new OneLevelDecisionTree(i,true);
            atths.add(t);
            OneLevelDecisionTree u = new OneLevelDecisionTree(i,false);
            atths.add(u);
        }
        OneLevelDecisionTree t = new OneLevelDecisionTree();
        t.attr = 0;
        t.left = true;
        t.right = true;
        atths.add(t);
        OneLevelDecisionTree u = new OneLevelDecisionTree();
        t.attr = 0;
        t.left = false;
        t.right = false;
        atths.add(u);
        
        return atths;
    }
    
    public void updateWeights (OneLevelDecisionTree root)
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
    public OneLevelDecisionTree selectTree()
    {
        double minError = 1;
        OneLevelDecisionTree choosenTree = new OneLevelDecisionTree();
        for (OneLevelDecisionTree root: hypothesisSpace)
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
        System.out.println("epsilon for this round: "+ epsilon);
        
        hypothesisSpace.remove(choosenTree);
        return choosenTree;
    }
    
    public void boost(int times)
    {
        int counter = 1;
        System.out.println("Round "+ counter);
        while(counter <= times) {
            OneLevelDecisionTree workingTree = selectTree();
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
        for (Map.Entry<OneLevelDecisionTree,Double> e: choosenTrees.entrySet())
        {
            if ( e.getKey().getOutcome(dp) == true)
                value = value + e.getValue();
            else
                value = value - e.getValue();
        }
        return value >= 0;
            
    }
    
    public double evaluate(Dataset dplst)
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
        for (Map.Entry<AttrLimitedDecisionTree,Double> e: choosenTrees.entrySet())
        { 
            System.out.println("Tree attributes: " + Arrays.toString(e.getKey().treeAttr) );
            System.out.println("Tree type: " + e.getKey().treetype);
            System.out.println("Tree outputs: " + Arrays.toString(e.getKey().treeOutput));
            System.out.println("Tree's alpha: " + e.getValue());
        }
    }
}
