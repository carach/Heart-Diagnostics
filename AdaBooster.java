import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Vector;

public class AdaBooster implements Hypothesis<Boolean> {
    HashMap<DataPoint<Boolean>,Double> weights = new HashMap<>();
    HashMap<Hypothesis,Double> chosenHypotheses;    
    double epsilon;

    public AdaBooster(Dataset<Boolean> trainSet){
        int m = trainSet.size();
        int n = trainSet.getFirst().x.length;
        for(DataPoint<Boolean>dp : trainSet)
            weights.put(dp, (double)1/m);
        chosenHypotheses = new HashMap<>();    
    }

    public void updateWeights (Hypothesis h)
    {
        if (h == null)
            return;
        
        if (epsilon >=0.5)
        {
            System.out.println("The learner is too weak...");
            return;
        }
        
        double alpha = 0.5 * Math.log((1 - epsilon)/epsilon);

        for (DataPoint<Boolean> dp: weights.keySet())
        {
            double tmp = weights.get(dp);
            
            if (dp.y == h.classify(dp))   // correctly classified
                weights.replace(dp,tmp * Math.pow(Math.E, - alpha) / (2 * Math.pow(epsilon * ( 1 - epsilon ),0.5)));
            else           
                weights.replace(dp,tmp * Math.pow(Math.E, alpha) / (2 * Math.pow(epsilon * ( 1 - epsilon ),0.5)));

        }
    }
    /* select a tree within hypothesis space which has a minimium weighted error.
    * 
    * @param Dataset
    * return the root of the select tree
    * also update epsilon
    */
    public Hypothesis selectHypothesis(Vector<Hypothesis> hypothesisSpace, Dataset<Boolean> trainSet)
    {
        double minError = 1;
        Hypothesis chosen = null;
        for (Hypothesis h: hypothesisSpace)
        {
            double accumError = 0;
            for (DataPoint<Boolean> dp: trainSet)
                if (dp.y != h.classify(dp))   // misclassified
                    accumError = accumError + weights.get(dp);
            if (accumError < minError)
            {
                minError = accumError;
                chosen = h;
            }
        }
        epsilon = minError;
        
        System.out.println("Hypothesis selected.");
        chosen.print();
        System.out.println("epsilon for this round: "+ epsilon);
        
        hypothesisSpace.remove(chosen);
        
        return chosen;
    }
    
    public void boost(Vector<Hypothesis> hypothesisSpace, Dataset<Boolean> trainSet, Dataset<Boolean> testSet, int times)
    {
        int counter = 1;
        System.out.println("Round "+ counter);
        while(counter <= times) {
            Hypothesis workingHp = selectHypothesis(hypothesisSpace, trainSet);
            chosenHypotheses.put(workingHp,0.5 * Math.log((1 - epsilon)/epsilon));
            updateWeights(workingHp);

        System.out.println("Accuracy on training set: " + evaluate(trainSet));  
        System.out.println("Accuracy on test set: " + evaluate(testSet));
        counter ++;
        }
    }

    public Boolean classify(DataPoint<Boolean> dp)
    {
        double value = 0;
        for (Entry<Hypothesis, Double> e: chosenHypotheses.entrySet())
        {
            if ( e.getKey().classify(dp) == true)
                value = value + e.getValue();
            else
                value = value - e.getValue();
        }
        return value >= 0;
            
    }
    
    public Double evaluate(Dataset<Boolean> dplst)
    {
        int corr = 0;
        for(DataPoint<Boolean> dp: dplst)
            if(dp.y == classify(dp))
                corr ++;
        return (double)corr/(double)dplst.size();
    }
    
    public void print()
    {
        System.out.println("Classifier: ");
        for (Entry<Hypothesis, Double> e: chosenHypotheses.entrySet())
        { 
            // System.out.println("Hypothesis attributes: " + Arrays.toString(e.getKey().treeAttr) );
            // System.out.println("Hypothesis type: " + e.getKey().treetype);
            // System.out.println("Hypothesis outputs: " + Arrays.toString(e.getKey().treeOutput));
            // System.out.println("Hypothesis's alpha: " + e.getValue());
            e.getKey().print();
        }
    }
}