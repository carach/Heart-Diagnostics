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
            Dataset trainSet = new Dataset(inputTrainFile,0,1,22,"1").convertToBoolean("1");
            Dataset testSet = new Dataset(inputTestFile,0,1,22,"1").convertToBoolean("1");
            
            
            // // train a multibranch decision tree
            // DecisionTree<Boolean> dtOfHD = new DecisionTree<>();
            // DecisionTree.DTreeNode<Boolean> root1;
            // root1 = dtOfHD.train(trainSet,10);
            // System.out.println("Traditional decision tree trained.");
            // dtOfHD.printDecisionTree(root1);
            // System.out.println("Accuracy on training set: " + dtOfHD.evaluate(trainSet, root1));
            // System.out.println("Accuracy on test set: " + dtOfHD.evaluate(testSet, root1));
            
            // train a binary decision tree
            BinaryDecisionTree HDbdt = new BinaryDecisionTree(trainSet);
            System.out.println("binary decision tree trained.");
            HDbdt.print();
            System.out.println("Accuracy on training set: " + HDbdt.evaluate(trainSet));
            System.out.println("Accuracy on test set: " + HDbdt.evaluate(testSet));

            
            // adaboost decision trees with a limited number of attributes. 
            AdaBooster ab = new AdaBooster(trainSet);
            System.out.println("Begin boosting on trees with no. of attributes less than 3... Number of rounds: 3.....................................");
            ab.boost(new AttrLimitedDecisionTree().enumerate(trainSet),trainSet, testSet,3);
            
            System.out.println("Begin boosting on trees with no. of attributes less than 3... Number of rounds: 10....................................");
            ab.boost(new AttrLimitedDecisionTree().enumerate(trainSet), trainSet, testSet, 10);
           
            // // coordinate decent on trees with height one
            // CoordinateDescent cd = new CoordinateDescent(trainSet,testSet);
            // System.out.println("Begin coordinate descend... Unlimited rounds................................");
            // cd.descent();
            // System.out.println("Final accuracy on test set: "+ cd.evaluate(testSet));
            // System.out.println("Alpha: " + cd.hypothesisSpace.values().toString());
            
            // adaboost decision trees with height one
            System.out.println("Begin boosting on trees with depth = 1... Number of rounds: 20......................................");
            ab.boost(new OneLevelDecisionTree().enumerate(trainSet), trainSet, testSet, 20);
            
        } 
        else {
	    System.out.println("Pleas specify input file");
	    }
    }
}
