import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

/*
 * import an input file
 * convert predictors to string array and response to boolean.
 * provide a method to convert Dataset of string array predictors to Dataset of boolean array predictors for those have binary predictors.
 */

/**
 *
 * @author Cara
 * 
 */
public class Dataset<T> extends LinkedList<DataPoint<T>>{

    // constructor: read data from a file to construct an object of Dataset<String>
    public Dataset () {}
    public Dataset (File inputFile,int outputIndex, 
            int inputStartIndex, int inputEndIndex, String labelToBeTrue) throws FileNotFoundException {
        Scanner in = new Scanner (inputFile);
        String line;
        String[] str;
        while(in.hasNextLine())
        {
            String[] m;
            m = new String[inputEndIndex - inputStartIndex + 1];
            boolean n;
            line = in.nextLine();
            str = line.split(",");
            for( int i = 0; i < m.length; i++)           
                m[i] = str[ i + inputStartIndex ].trim();               
            if (str[outputIndex].equals(labelToBeTrue))
                n = true;
            else
                n = false;   
            this.add(new DataPoint(m,n));
        }
        in.close();
    }
    
    /**
     *
     * @param labelToBeTrue
     * @return a new Dataset<Boolean>
     */
    public Dataset<Boolean> convertToBoolean(String labelToBeTrue)
    {
        Dataset<Boolean> bds = new Dataset();
        
        for(DataPoint dp: this) {
            Boolean[] m = new Boolean[dp.x.length];
            for( int i = 0; i < m.length; i++)
                m[i] = dp.x[i].equals(labelToBeTrue);
            bds.add(new DataPoint(m,dp.y));
        }
        return bds;
    }
}

