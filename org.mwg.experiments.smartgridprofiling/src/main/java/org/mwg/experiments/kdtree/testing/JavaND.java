package org.mwg.experiments.kdtree.testing;

import org.mwg.Callback;

/**
 * Created by assaad on 05/08/16.
 */
public class JavaND implements DummyNode{
    private JavaND _left;
    private JavaND _right;
    private int _id;
    private int _level;
    private static int counter=0;


    public JavaND(){
        _id=counter;
        counter++;
    }

    public Object createNode(){
        return new JavaND();
    }


    public void setLeft(Object left){
        JavaND l= (JavaND) left;
        l._level=this._level+1;
        this._left=l;
    }

    public void setRight(Object right){
        JavaND l= (JavaND) right;
        l._level=this._level+1;
        this._right=l;
    }


    public void traverseRec(Callback<Boolean> callback){
        reccursive(this, 0);
        callback.on(true);
    }

    @Override
    public void print() {
        int r=-1;
        int l=-1;
        if(_left!=null){
            l=_left._id;
        }
        if(_right!=null){
            r=_right._id;
        }
        System.out.println("node "+_id+", _lev: "+_level+" left: "+l+", right: "+r);
    }


    private static void reccursive(JavaND root, int lev){
        //Step 1
        System.out.println("S1: "+root._id+ " _lev: "+root._level+" received: "+lev);

        JavaND near=root._left;
        JavaND far=root._right;

        if(near!=null){
            reccursive(near,lev+1);
        }

        System.out.println("S2: "+root._id+ " _lev: "+root._level+" received: "+lev);

        if(far!=null){
            reccursive(far,lev+1);
        }

        System.out.println("S3: "+root._id+ " _lev: "+root._level+" received: "+lev);
    }
}
