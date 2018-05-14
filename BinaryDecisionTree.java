import java.util.ArrayList;
import java.util.LinkedList;
/*
 * both predictors and response is binary
 * clean dataset
 * non-leaf node has a working attribute
 * each attribute only splits the data once
 * leaf node predict the response value
 * unlimited tree depth
 */

/**
 *
 * @author Cara
 */
public class BinaryDecisionTree implements Hypothesis<Boolean>  {
    BDTNode root;
    ArrayList<Integer> workingAttr;   // the list of all the participant attributes
    public BinaryDecisionTree (Dataset<Boolean> trainSet) {
        workingAttr = new ArrayList<>();
        root = new BDTNode(trainSet, null);
    }
    public BinaryDecisionTree () {}
    public BinaryDecisionTree (int num) {   // specify the number of attibute which are going to train the tree
        workingAttr = new ArrayList<>(num);
    }
    public class BDTNode {   // decision binary tree node
        int attr;   // when attr = -1, it is a leaf; when attr >= 0, it specifies the working attribute and mVote makes sense for its children or for a fixed depth decision tree
        BDTNode left;
        BDTNode right;
        Boolean mVote;

        public BDTNode () {}
        public BDTNode (Dataset<Boolean> ds, Boolean vote) {     // this constructor recursively construct all the nodes of the whole tree

            if (ds == null || ds.isEmpty()) {   // If a partition contains no data points, use the majority vote at its parent, and make the node a leaf.
                this.mVote = vote;   // mVote as the outcome value when it's a leaf node
                attr = -1;     // make it a leaf node
                return;
            }
            int attrLength = ds.getFirst().x.length;
            Dataset<Boolean> leftDs = new Dataset<>();
            Dataset<Boolean> rightDs = new Dataset<>();
            double maxConEn = Double.MAX_VALUE;    // the max conditional entropy
            for (int i = 0; i < attrLength; i++) {
                if (BinaryDecisionTree.this.workingAttr.contains(i))   // if this attr. has been recursed on before, skip it
                    continue;
                Dataset<Boolean> currLeftDs = new Dataset<>();
                Dataset<Boolean> currRightDs = new Dataset<>();
                int[][] count = new int[][]{
                    {0,0},{0,0}
                };

                for (DataPoint<Boolean> dp: ds) {   // scan the whole dataset
                    if (dp.x[i]) {
                        currRightDs.add(dp);    // temporarily add the data point to its right child sub dataset
                        if (dp.y) 
                            count[1][1]++;
                        else
                            count[1][0]++;
                    } else {
                        currLeftDs.add(dp);     // temporarily add the data point to its left child sub dataset
                        if (dp.y) 
                            count[0][1]++;
                        else
                            count[0][0]++;
                    }
                }
                if ( (count[1][1] + count[0][1]) * (count[1][0] + count[0][0]) == 0) {    // This is the case the current set is "pure", the path could end.
                    attr = -1;     // this is just the attribute this node is working on
                    this.mVote = (count[1][1] + count[0][1]) > (count[1][0] + count[0][0]);
                    return;
                } else { // compute the conditional entropy on this attribute
                    double currConEn = 0;
                    double en[][] = new double[2][2];
                    for (int j = 0; j < 2; j++)
                        for (int k = 0; k < 2; k++) {
                            if (count[j][k] == 0) {
                                en[j][k] = 0;
                            } else {
                                en[j][k] = - (double)(count[j][1] + count[j][0]) / (double)ds.size() * (double)count[j][k]/(double)(count[j][1] + count[j][0]) * Math.log((double)count[j][k]/(double)(count[j][1] + count[j][0])) / Math.log(2);
                            }
                            currConEn += en[j][k];
                        }
                    // System.out.println(currConEn);
                    if ( maxConEn > currConEn) {    // smaller conditional entropy found
                        maxConEn = currConEn;
                        attr = i;
                        leftDs = currLeftDs;
                        rightDs = currRightDs;
                        this.mVote = (count[1][1] + count[0][1]) >= (count[1][0] + count[0][0]);   // set majority vote result for this node
                    }
                }
                // System.out.println("statistic: " + count[1][1]+ "," + count[0][1] + "," + count[1][0] + "," + count[0][0] + " mVote:" + this.mVote);
            }
            if (maxConEn < Double.MAX_VALUE) {   // a new attribute as a inner node is found
                BinaryDecisionTree.this.workingAttr.add(attr);
                left = new BDTNode(leftDs, this.mVote);     // recursively generate left child
                right = new BDTNode(rightDs, this.mVote);   // recursively generate right child
            } else {    // no more attributes could be recursed on
                attr = -1;
                int[] count = new int[]{0,0};
                for (DataPoint<Boolean> dp: ds) {   // scan the whole dataset
                    if (dp.y)
                        count[1]++;
                    else
                        count[0]++;
                }
                this.mVote = count[1] > count[0];
                return;
            // System.out.println(mVote);
            }
            
        }
    }

    // print the whole tree
    public void print() {
        print(this.root);
    }

    // recursively print the tree from the specific node
    public void print(BDTNode root) {
        if (this.root == null)
            return;
        System.out.println("Attribute Index: "+ root.attr);
        if (root.attr == -1)
            System.out.println("Value: " + root.mVote);
        else {
            print(root.left);
            print(root.right);
        }
    }
    public Boolean classify(DataPoint<Boolean> dp) {
        if (dp == null || this.root == null)
            return null;
        // boolean outcome;
        BDTNode curr = this.root;
        // BDTNode parent = new BDTNode();
        while(curr.attr > -1) {
            if (curr.attr >= dp.x.length) {
                return null;
            }
            // parent = curr;
            curr = dp.x[curr.attr]? curr.right : curr.left;
        }
        return curr.mVote;
    }
    public Double evaluate(Dataset<Boolean> ds)
    {
        int corr = 0;
        if (ds == null)
            return null;
        for(DataPoint<Boolean> dp: ds) {
            if (classify(dp) == null) 
                return null;
            else if (classify(dp).equals((Boolean)dp.y))
                corr++;
        }     
        return (double)corr/(double)ds.size();
    }
}
