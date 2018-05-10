
/*
 * an interface for hypothesis
 * hypothesis for binary classification problems
 */

/**
 *
 * @author Cara
 */


public interface Hypothesis<T> {
    void print();
    Boolean classify(DataPoint<T> dp);
    Double evaluate(Dataset<T> ds);
}