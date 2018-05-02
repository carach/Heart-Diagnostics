
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mousie
 */
public class HeartDiagnosis {
    
    public static void main(String[] args) throws FileNotFoundException
    { 
        
        if (args.length > 0) {

            File inputTrainFile = new File(args[0]);
            File inputTestFile = new File(args[1]);
            DataPoint<Boolean> dp = new DataPoint<>();
            LinkedList<DataPoint<Boolean>> dplstTrain = dp.convertToBoolean(dp.buildDatapointList(inputTrainFile,0,1,22,"1"), "1");
	    LinkedList<DataPoint<Boolean>> dplstTest = dp.convertToBoolean(dp.buildDatapointList(inputTestFile,0,1,22,"1"), "1");
            
            // train a traditional decision tree
            DecisionTree<Boolean> dtOfHD = new DecisionTree<>();
            DecisionTree.DTreeNode<Boolean> root1;
            root1 = dtOfHD.trainDecistionTree(dplstTrain,10);
            dtOfHD.printDecisionTree(root1);
            System.out.println("Accuracy on training set: " + dtOfHD.evaluate(dplstTrain, root1));
            System.out.println("Accuracy on test set: " + dtOfHD.evaluate(dplstTest, root1));
            
            // adaboost decision trees with a limited number of attributes.
            
            AdaBoostingOnALtree ab3 = new AdaBoostingOnALtree(dplstTrain,dplstTest);
            System.out.println("Begin boosting... Number of rounds: 3.....................................");
            ab3.boost(3);
            
            AdaBoostingOnALtree ab10 = new AdaBoostingOnALtree(dplstTrain,dplstTest);
            System.out.println("Begin boosting... Number of rounds: 10....................................");
            ab10.boost(10);
           
            // coordinate decent on  trees with height one
            CoordinateDescent cd = new CoordinateDescent(dplstTrain,dplstTest);
            System.out.println("Begin coordinate descend... Unlimited rounds................................");
            cd.descent();
            System.out.println("Final accuracy on test set: "+ cd.evaluate(dplstTest));
            System.out.println("Alpha: " + cd.hypothesisSpace.values().toString());
            
            // adaboost decision trees with height one
            AdaBoostingOnOLtree ab20 = new AdaBoostingOnOLtree(dplstTrain,dplstTest);
            System.out.println("Begin boosting... Number of rounds: 20......................................");
            ab20.boost(20);
            System.out.println("Alpha: " + ab20.choosenTrees.values().toString());
            
        } 
        else {
	    System.out.println("Pleas specify input file");
	}
    }
}
