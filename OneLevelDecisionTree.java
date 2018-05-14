import java.util.ArrayList;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Cara
 */
public class OneLevelDecisionTree extends BinaryDecisionTree{
    
    public OneLevelDecisionTree(){}
    public OneLevelDecisionTree(int attr, boolean output)
    { 
        root = new BDTNode();
        root.attr = attr;
        root.left = new BDTNode();
        root.left.attr = -1;
        root.left.mVote = output;
        root.right = new BDTNode();
        root.right.attr = -1;
        root.right.mVote = !output;
    }
    @Override
    public Boolean classify(DataPoint<Boolean> dp)
    {
        if (dp == null)
            return null;
        if (!dp.x[root.attr])
            return root.left.mVote;
        else
            return root.right.mVote;
    }
    
    public ArrayList<OneLevelDecisionTree> enumerate(Dataset<Boolean> ds) {
        ArrayList<OneLevelDecisionTree> hypothesisSpace = new ArrayList<>();
        int length = ds.getFirst().x.length;
        for( int i = 0; i < length; i ++)
        {
            OneLevelDecisionTree t = new OneLevelDecisionTree(i,true);
            hypothesisSpace.add(t);
            OneLevelDecisionTree u = new OneLevelDecisionTree(i,false);
            hypothesisSpace.add(u);
        }
        OneLevelDecisionTree t = new OneLevelDecisionTree();
        t.root = new BDTNode();
        t.root.attr = 0;
        t.root.left = new BDTNode();
        t.root.left.attr = -1;
        t.root.left.mVote = true;
        t.root.right = new BDTNode();
        t.root.right.attr = -1;
        t.root.right.mVote = true;
        hypothesisSpace.add(t);
        OneLevelDecisionTree u = new OneLevelDecisionTree();
        u.root = new BDTNode();
        u.root.attr = 0;
        u.root.left = new BDTNode();
        u.root.left.attr = -1;
        u.root.left.mVote = false;
        u.root.right = new BDTNode();
        u.root.right.attr = -1;
        u.root.right.mVote = false;
        hypothesisSpace.add(u);
        return hypothesisSpace;
    }
}
