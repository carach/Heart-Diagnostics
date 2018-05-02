

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
public class CoordinateDescent {
    LinkedList<DataPoint<Boolean>> dptraininglst;
    LinkedList<DataPoint<Boolean>> dptestlst;
    
    HashMap<OneLevelDecisionTree,Double> hypothesisSpace;       
    
    public CoordinateDescent(LinkedList<DataPoint<Boolean>> dplstTrain, LinkedList<DataPoint<Boolean>> dplstTest){
        dptraininglst = dplstTrain;
        dptestlst = dplstTest;
        hypothesisSpace = addTreeToHypothesisSpace();
    }
    public static HashMap<OneLevelDecisionTree,Double> addTreeToHypothesisSpace()
    {
        HashMap<OneLevelDecisionTree,Double> atths = new HashMap<>();
        for( int i = 0; i < 22; i ++)
        {
            OneLevelDecisionTree t = new OneLevelDecisionTree(i,true);
            atths.put(t,0.0);
//            OneLevelDecisionTree u = new OneLevelDecisionTree(i,false);
//            atths.put(u,0.0);
        }
        OneLevelDecisionTree t = new OneLevelDecisionTree();
        t.attr = 0;
        t.left = true;
        t.right = true;
        atths.put(t,0.0);
//        OneLevelDecisionTree u = new OneLevelDecisionTree();
//        t.attr = 0;
//        t.left = false;
//        t.right = false;
//        atths.put(u,0.0);
        
        return atths;
    }
    
    public void descent()
    {
        descent(Integer.MAX_VALUE);
    }
    public void descent(int rounds)
    {   
        int count = 0;
        double exploss = Double.MAX_VALUE;
        int converged = 0;
        
        while(count < rounds )
        {
            for (Entry<OneLevelDecisionTree,Double> f: hypothesisSpace.entrySet())
            {
                OneLevelDecisionTree t = f.getKey();
                double numer = 0;
                double denorm = 0;
                double n = 0;
                double d = 0;
            
                for (DataPoint<Boolean> dp: dptraininglst)
                {
                    double miss = 0;
                    double hit = 0;

                    if (dp.y == t.getOutcome(dp))
                    {
                        for (Entry<OneLevelDecisionTree,Double> e: hypothesisSpace.entrySet())
                        {
                            if ( e == t)
                                continue;
                            boolean output = e.getKey().getOutcome(dp);
                            if (dp.y == output)
                                hit = hit - e.getValue();
                            else
                                hit = hit + e.getValue();
                        }
                        n = n + Math.pow(Math.E, hit);
                        hit = hit + f.getValue();
                        numer = numer + Math.pow(Math.E, hit);
                    }
                    else
                    {
                        for (Entry<OneLevelDecisionTree,Double> e: hypothesisSpace.entrySet())
                        {
                            boolean output = e.getKey().getOutcome(dp);
                            if (dp.y == output)
                                miss = miss - e.getValue();
                            else
                                miss = miss + e.getValue();
                        }
                        d = d + Math.pow(Math.E, miss);
                        miss = miss - f.getValue();
                        denorm = denorm + Math.pow(Math.E, miss);
                    }  
                }

                double tmp  = numer + n + denorm + d;
                if ( d + n - exploss == 0)
                    converged++ ;
                exploss = d + n;
                
                hypothesisSpace.put(t, 0.5 * Math.log( numer / denorm));
                System.out.println("Exponential loss = "+ exploss);
                System.out.println("Accuracy on training set: " + evaluate(dptraininglst));
                System.out.println("Accuracy on test set: " + evaluate(dptestlst));
            }
            count ++;
            if (converged> 10 || count > rounds)
            {
                System.out.println("Total runing rounds: " + count);
                break;
            }
        }  
    }
    
    public boolean getOutcome(DataPoint<Boolean> dp)
    {
        double value = 0;
        for (Entry<OneLevelDecisionTree,Double> e: hypothesisSpace.entrySet())
        {
            if ( e.getKey().getOutcome(dp) == true)
                value = value + e.getValue();
            else
                value = value - e.getValue();
        }
        return value > 0;           
    }
    
    public double evaluate(LinkedList<DataPoint<Boolean>> dplst)
    {
        int corr = 0;
        for( DataPoint<Boolean> dp: dplst)
            if(dp.y == getOutcome(dp))
                corr ++;
        return (double)corr/(double)dplst.size();
    }
}
