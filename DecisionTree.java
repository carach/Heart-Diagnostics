
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Decision Tree by greedy heuristic based approach
 * @author Cara
 * 
 */
public class DecisionTree<T> {

    public static class DTreeNode<T> {
        boolean outputVal;
        boolean majorityVote;   // in case no match for the new point, need to depend on majority vote
        int attrIndex;    // indicate which attibute it is working on
        boolean isLeaf;     // tell if the node is the last level of a path to make final dicision, only when isLeaf is true the outputVal makes sense
        DTreeNode<T> parent;    
        ArrayList<Integer> usedAttr = new ArrayList<>();
        HashMap<T, Dataset> hmlst = new HashMap<>();      // map a label to its subset of training data
        HashMap<T, DTreeNode> children = new HashMap<>();

        public DTreeNode(int x) {
            attrIndex = x;
            ArrayList<Integer> usedAttr = new ArrayList<>();
            HashMap<T, Dataset> hmlst = new HashMap<>();      // map a label to its subset of training data
            HashMap<T, DTreeNode> children = new HashMap<>();
        }
        
        DTreeNode() {
            ArrayList<Integer> usedAttr = new ArrayList<>();
            HashMap<T, Dataset> hmlst = new HashMap<>();      // map a label to its subset of training data
            HashMap<T, DTreeNode> children = new HashMap<>();
        }
    }
    
    // helper inner class to compute entropy of an identical lable under an attribute
    public class Counter{
        T label;
        int num = 0;
        int tr = 0;
        int fs = 0;
        Dataset newlst = new Dataset();   // put satisfied datapoint into this new list

    }
    /*
    /* helper method: to find the index of the minimum element in an array
    /* @para double[]
    */
    public int findMinIndex(double[] array)
    {
        if (array.length == 0)
            return -1;
        double min = array[0];
        int index = 0;
        for (int i = 1; i < array.length; i++)
        {
            if ( array[i] < min)
            {
                min = array[i];
                index = i;
            }
        }
        return index;
    }
    
    /* helper method to generate a new tree node given a data set
    /* compute the conditional entropy on each artribute of the datapoint set of that node for generate its children
    /* @para Dataset
    */

    public DTreeNode<T> nodeBuilder(Dataset dplst){ 
        int tr = 0; // counter for y = true;
        int fs = 0; // counter for y = false; 
        double HY;
        
        DTreeNode<T> newNode = new DTreeNode<>();
        
        HashMap<T, Counter>[] attrihm = new HashMap[dplst.getFirst().x.length];
        for (int i = 0; i < attrihm.length; i++)    // initialize the array of HashMap to avoid NullPointException
            attrihm[i] = new HashMap<T, Counter>();
        double[] dimHYX = new double[attrihm.length];
        Counter cnt;
        
        // build an array of hashmap mapping distinct lables to their statitics
        for (DataPoint<T> dp: dplst)
        {
            if (dp.y == true)
                tr++;
            else
                fs++;
            for (int i = 0; i < dp.x.length; i++)
            {
                if (newNode.usedAttr.contains(i))   // if this attr. has been recursed on before, skip it
                    continue;
                if (!attrihm[i].containsKey(dp.x[i]))
                attrihm[i].put( dp.x[i], new Counter());
                
                cnt = attrihm[i].get(dp.x[i]);
                cnt.num++;
                if (dp.y == true)
                    cnt.tr++;
                else
                    cnt.fs++;
            }
    
        }
        newNode.majorityVote = tr >= fs? true : false;   // set majority vote result for this node
        
//        HY = (-1) * ((double)e/(double)dplst.size()* Math.log((double)e/(double)dplst.size()) / Math.log(2)
//                + (double)p/(double)dplst.size()* Math.log((double)p/(double)dplst.size())/ Math.log(2));

        if ( tr * fs == 0) // This is the case the current set is "pure", the path is at its end.
        {
            newNode.isLeaf = true;
            newNode.outputVal = tr > 0;
            newNode.attrIndex = -1;     // define leaf node's attrIndex = -1 to avoid confusion
        }
        
        else
        {
            // compute the conditional entropy for each distinct lable
            for(int i = 0; i < attrihm.length; i++)
            {

                double tmp = 0;
                Collection<Counter> lst= attrihm[i].values();
                if (lst.isEmpty())
                    tmp = 2;
                else
                    for (Counter ct: lst)
                        if (ct.tr * ct.fs != 0)   // avoid caculating log0 and get an NaN
                            tmp = tmp - (double)ct.num / (double)dplst.size() * ((double)ct.tr/(double)ct.num * Math.log((double)ct.tr/(double)ct.num) / Math.log(2)
                                + (double)ct.fs/(double)ct.num * Math.log((double)ct.fs/(double)ct.num) / Math.log(2));

                dimHYX[i] = tmp;
            }

            int choosen = findMinIndex(dimHYX);     // choose the attribute with the minimum conditional entropy to work on
            newNode.attrIndex = choosen;
            for(DataPoint<T> dp: dplst)    // traverse the training set to build the data point subsets for every distinct label belonging to the choosen attibute
            {
                cnt = attrihm[choosen].get(dp.x[choosen]);

                cnt.newlst.add(dp);
            }
            Collection<T> str = attrihm[choosen].keySet();
            for (T s : str)        // trasfer the data point subsets to newNode as its children's subsets.
                newNode.hmlst.put(s, attrihm[choosen].get(s).newlst);

            newNode.usedAttr.add(choosen);
    }
        return newNode;
        
    }
    
            
    /* core method to train the tree
    * begin with the whole training data set
    * @param Dataset
    * return the root of the learned tree
    */
    public DTreeNode train(Dataset trainlist) {
        return train(trainlist,Integer.MAX_VALUE);
    }
    
    public DTreeNode train(Dataset trainlist,int maxDepth) {     
        DTreeNode<T> root = nodeBuilder(trainlist);
        DTreeNode<T> curr = root;
        
        while(!curr.hmlst.isEmpty() || curr.parent!= null)  // the first condition is the case the subsets of this node has all been processed;
                                                            // the seconde consition is the case all braches are done.
        {
            if (curr.hmlst.isEmpty() || curr.usedAttr.size() == maxDepth)   
            {
                curr = curr.parent;
                continue;
            }
            HashMap.Entry<T, Dataset> entry = curr.hmlst.entrySet().iterator().next();
            T workingLabel = entry.getKey();
            Dataset workinglst = curr.hmlst.remove(workingLabel);
            DTreeNode newNode = nodeBuilder(workinglst);

            curr.children.put(entry.getKey(), newNode);
            newNode.parent = curr;
            newNode.usedAttr.addAll(newNode.parent.usedAttr);

            curr = newNode;
        }
        System.out.println("Decision tree built completed.");
         
        return root;
                
    }
    
    /* recursively print out the decision tree level by level (but not precisely), given the root of the tree
    * @para DTreeNode<T>
    */
    public void printDecisionTree(DTreeNode<T> root)
    {
        if (root == null)
            return;
        System.out.println("Attribute Index: "+ root.attrIndex );
        for(Object str: root.children.keySet() )
        {
            DTreeNode<T> nd = (DTreeNode) root.children.get(str);
            if (nd.isLeaf == true)
                System.out.println(str.toString() + ":"+ nd.outputVal);
            else 
                printDecisionTree(nd);
        }
    }
    /* method to test accuray on a given data set using the trained decision tree
    * @para LinkedList<DataPoint<T>>, DTreeNode<T>
    * return the accuracy
    */
    public double evaluate(Dataset dplst, DTreeNode<T> root)
    {
        int corr = 0;
        if (dplst == null || root == null)
            return 0;
        
        for(DataPoint<T> dp: dplst)   
            if (dp.y == predict(dp, root))
                corr++;
        
        return (double)corr/(double)dplst.size();
    }
    public Boolean predict(DataPoint<T> dp, DTreeNode<T> root)
    {
        if (dp == null || root == null)
            return null;
        
        boolean outcome;
        DTreeNode<T> curr = root;
        DTreeNode<T> tmp = new DTreeNode<>();
        while(curr != null && !curr.isLeaf )
            if (!curr.children.isEmpty()){
                tmp = curr;
                curr = curr.children.get(dp.x[curr.attrIndex]);
            }
            else
                break;
        if (curr == null)
            outcome = tmp.majorityVote;
        else if (curr.isLeaf)
            outcome = curr.outputVal;      // patition contains no data points, use the majority vote at its parent
        else
            outcome = curr.majorityVote;
        return outcome;
    }
}
