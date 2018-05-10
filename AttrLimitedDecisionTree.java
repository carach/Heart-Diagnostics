import java.util.Arrays;
import java.util.Vector;
import java.util.ArrayList;
import java.util.LinkedList;

/*
 * train a decision tree only with given number of attributes.
 * no longer heuristic greedy algorithm
 */

/**
 *
 * @author Cara
 */
public class AttrLimitedDecisionTree extends BinaryDecisionTree{

    
    String treetype;
    int[] treeAttr;
    boolean[] treeOutput;
    public AttrLimitedDecisionTree(){}
    public AttrLimitedDecisionTree(int[] attrSet,String type,boolean[] outputCombi)
    {
        
        root = new BDTNode();
        if ( attrSet == null || type == null || outputCombi == null) {}
        
        root.attr = attrSet[0];
        root.left = new BDTNode();
        root.right = new BDTNode();
        
        // just for print out;
        treetype = type;
        treeAttr = attrSet;
        treeOutput = outputCombi;
        
        switch(type){
            case "balance": root.left.attr = attrSet[1];
                            root.left.left = new BDTNode();
                            root.left.left.mVote = outputCombi[0];
                            root.left.right = new BDTNode();
                            root.left.right.mVote = outputCombi[1];
                            root.right.attr = attrSet[2];
                            root.right.left = new BDTNode();
                            root.right.left.mVote = outputCombi[2];
                            root.right.right = new BDTNode();
                            root.right.right.mVote = outputCombi[3];
                            break;
            case "leftZigzig": root.left.attr = attrSet[1];
                            root.right.mVote = outputCombi[0];
                            root.left.left = new BDTNode();
                            root.left.left.attr = attrSet[2];
                            root.left.right = new BDTNode();
                            root.left.right.mVote = outputCombi[1];
                            root.left.left.left = new BDTNode();
                            root.left.left.left.mVote = outputCombi[2];
                            root.left.left.right = new BDTNode();
                            root.left.left.right.mVote = outputCombi[3];
                            break;
            case "rightZigzig":root.right.attr = attrSet[1];
                            root.left.mVote = outputCombi[0];
                            root.right.right = new BDTNode();
                            root.right.right.attr = attrSet[2];
                            root.right.left = new BDTNode();
                            root.right.left.mVote = outputCombi[1];
                            root.right.right.right = new BDTNode();
                            root.right.right.right.mVote = outputCombi[2];
                            root.right.right.left = new BDTNode();
                            root.right.right.left.mVote = outputCombi[3];
                            break;
            case "leftZigzag": root.left.attr = attrSet[1];
                            root.right.mVote = outputCombi[0];
                            root.left.left = new BDTNode();
                            root.left.left.mVote = outputCombi[1];
                            root.left.right = new BDTNode();
                            root.left.right.attr = attrSet[2];
                            root.left.right.left = new BDTNode();
                            root.left.right.left.mVote = outputCombi[2];
                            root.left.right.right = new BDTNode();
                            root.left.right.right.mVote = outputCombi[3];
                            break;
            case "rightZigzag": root.right.attr = attrSet[1];
                            root.left.mVote = outputCombi[0];
                            root.right.right = new BDTNode();
                            root.right.right.mVote = outputCombi[1];
                            root.right.left = new BDTNode();
                            root.right.left.attr = attrSet[2];
                            root.right.left.right = new BDTNode();
                            root.right.left.right.mVote = outputCombi[2];
                            root.right.left.left = new BDTNode();
                            root.right.left.left.mVote = outputCombi[3];
                            break;
                       
    }
    }
    
    @Override
    public Boolean classify(DataPoint<Boolean> dp)
    {
        if (dp == null || root == null)
            return null;
        BDTNode curr = this.root;
        while(curr.left != null && curr.right != null)
            if (dp.x[curr.attr])
                curr = curr.right;
            else
                curr = curr.left;
        return curr.mVote;
    }
    @Override
    public void print()
    {
        System.out.println("Attributes: " + Arrays.toString(this.treeAttr)  + " " + "type: " + "Output; " + this.treetype + " " + Arrays.toString(this.treeOutput));
    }
    
    // method to construct a hypothesis space enummerating all the trees
    // @param the number of the attributes
    public Vector<AttrLimitedDecisionTree> enummerate(Dataset<Boolean> ds) {
        Vector<AttrLimitedDecisionTree> hypothesisSpace = new Vector<>();

        ArrayList<int[]> attrSet = new ArrayList<>();   // the combination of all  3 attributes
        int n = ds.getFirst().x.length;
        if ( n < 1)
            return null;
        for(int i = 0; i < n; i ++)
            for ( int j = 0; j < n; j ++)
                for ( int k = 0; k < n; k ++)
                {
                    int[] a = {i,j,k};
                    attrSet.add(a);
                }    
        
        String[] typelst = new String[]{"balance", "leftZigzig", "leftZigzag", "rightZigzag"};

        LinkedList<boolean[]> outputLst = new LinkedList<>(); // the combination of all outputs
        for(int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++)
                    for (int l = 0; l < 2; l++)
                    {
                        boolean[]e = {(i==1),(j==1),(k==1),(l==1)};
                        outputLst.add(e);
                    }
        
        for (int[] a: attrSet)
            for (String t: typelst)
                for (boolean[] ot: outputLst)
                {
                    AttrLimitedDecisionTree ad = new AttrLimitedDecisionTree(a,t,ot);
                    hypothesisSpace.add(ad);
                }
        return hypothesisSpace;
    }
}
