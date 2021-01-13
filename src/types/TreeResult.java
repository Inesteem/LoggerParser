package src.types;

import com.sun.source.tree.Tree;

public class TreeResult {
    public int num;
    public double val;

    public TreeResult(int num, double val) {
        this.num = num;
        this.val = val;
    }

    public TreeResult() {
        this.num = 0;
        this.val = 0;
    }

    public void add(TreeResult other){
        num += other.num;
        val = other.val;
    }
}
