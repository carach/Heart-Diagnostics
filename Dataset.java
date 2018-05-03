import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

/*
 * import an input file
 * convert predictors to string array and response to boolean.
 * provide a method to convert DataSet of string array predictors to DataSet of boolean array predictors.
 */

/**
 *
 * @author Cara
 * @param <T>
 */
public class Dataset extends LinkedList<DataPoint>{

    // constructor: read data from a file to construct an object of Dataset<String>
    public Dataset () {}
    public Dataset (File inputFile,int outputIndex, 
            int inputStartIndex, int inputEndIndex, String labelToBeTrue) throws FileNotFoundException {
        Scanner in = new Scanner (inputFile);
        LinkedList<DataPoint<String>> ds  = new LinkedList<>();
        String line;
        String[] str;
        while(in.hasNextLine())
        {
            String[] m;
            m = new String[inputEndIndex - inputStartIndex + 1];
            String tmp;
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
    }
    
    /**
     *
     * @param splst
     * @param labelToBeTrue
     * @return
     */
    public Dataset convertToBoolean(String labelToBeTrue)
    {
        if( this == null)
            return null;
        Dataset bds = new Dataset();
        
        for(DataPoint<Boolean> dp: this) {
            Boolean[] m = new Boolean[dp.x.length];
            for( int i = 0; i < m.length; i++)
                m[i] = dp.x[i].equals(labelToBeTrue);
            bds.add(new DataPoint(m,dp.y));
        }
        return bds;
    }
}

