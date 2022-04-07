import LABA_2.LABA_2_CORE;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LABA_2_TEST {
    // input
    private static SparkSession sc = LABA_2_CORE.CreateSession();
    private static List<String> input_data_list = Arrays.asList(
            "122 1000 500 1",
            "123 1000 500 1",
            "122 1000 500 3",
            "122 1001 500 1");
    private static ArrayList<String> vocab = new ArrayList<String>(Arrays.asList(
            "ONE",
            "TWO",
            "THREE"));

    @Test
    public void Test_reduce(){
        // System.out.println(vocab); // DEBUG
        List<Tuple2<String, Integer>> tuple_pairs = LABA_2_CORE.Reduce(sc, input_data_list, vocab);
        System.out.println(tuple_pairs.get(0).toString()); // DEBUG
        assert tuple_pairs.get(0).toString().equals("(122,'ONE',2)");
        assert tuple_pairs.get(1).toString().equals("(122,'THREE',1)");
        assert tuple_pairs.get(2).toString().equals("(123,'ONE',1)");
    }
}
