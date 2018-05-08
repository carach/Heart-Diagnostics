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
            
            
            // // train a traditional decision tree
            // DecisionTree<Boolean> dtOfHD = new DecisionTree<>();
            // DecisionTree.DTreeNode<Boolean> root1;
            // root1 = dtOfHD.train(trainSet,10);
            // System.out.println("Traditional decision tree trained.");
            // dtOfHD.printDecisionTree(root1);
            // System.out.println("Accuracy on training set: " + dtOfHD.evaluate(trainSet, root1));
            // System.out.println("Accuracy on test set: " + dtOfHD.evaluate(testSet, root1));
            
            // train a binary decision tree
            BinaryDecisionTree HDbdt = new BinaryDecisionTree();
            System.out.println(trainSet.getFirst().x.length);
            HDbdt.root = HDbdt.new BDTNode(trainSet, null);
            System.out.println("binary decision tree trained.");
            HDbdt.print(HDbdt.root);
            System.out.println("Accuracy on training set: " + HDbdt.evaluate(trainSet));
            System.out.println("Accuracy on test set: " + HDbdt.evaluate(testSet));

            
            // adaboost decision trees with a limited number of attributes. 
            AdaBoostingOnALtree ab3 = new AdaBoostingOnALtree(trainSet,testSet);
            System.out.println("Begin boosting... Number of rounds: 3.....................................");
            ab3.boost(3);
            
            AdaBoostingOnALtree ab10 = new AdaBoostingOnALtree(trainSet,testSet);
            System.out.println("Begin boosting... Number of rounds: 10....................................");
            ab10.boost(10);
           
            // coordinate decent on  trees with height one
            CoordinateDescent cd = new CoordinateDescent(trainSet,testSet);
            System.out.println("Begin coordinate descend... Unlimited rounds................................");
            cd.descent();
            System.out.println("Final accuracy on test set: "+ cd.evaluate(testSet));
            System.out.println("Alpha: " + cd.hypothesisSpace.values().toString());
            
            // adaboost decision trees with height one
            AdaBoostingOnOLtree ab20 = new AdaBoostingOnOLtree(trainSet,testSet);
            System.out.println("Begin boosting... Number of rounds: 20......................................");
            ab20.boost(20);
            System.out.println("Alpha: " + ab20.choosenTrees.values().toString());
            
        } 
        else {
	    System.out.println("Pleas specify input file");
	}
    }
}
