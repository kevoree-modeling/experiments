package org.mwg.experiments.kdtree.testing;

import org.mwg.Callback;

/**
 * Created by assaad on 05/08/16.
 */
public interface DummyNode {
    void setLeft(Object left);
    void setRight(Object right);
    Object createNode();
    void traverseRec(Callback<Boolean> callback);
    void print();

}
