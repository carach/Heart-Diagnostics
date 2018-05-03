public class DataPoint<T> {
        T[] x;
        boolean y;
        
        public DataPoint(){}
        public DataPoint( T[] m, boolean n){
            x = m;
            y = n;
        }
    }