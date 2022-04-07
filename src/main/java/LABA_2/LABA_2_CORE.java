package LABA_2;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

import java.util.ArrayList;
import java.util.List;

/**
 * Core of LABA_2 - main reduce
 */
@Slf4j
public class LABA_2_CORE {

    /**
     * Collect RDD and upload to Cassandra Table
     * @param tuple_pairs - input tuples
     * @param cassandra_session - Cassandra session to upload rdd.collect()
     */
    public static void Upload(List<Tuple2<String, Integer>> tuple_pairs, Session cassandra_session){
        log.info("Save Result");
        for (Tuple2<String, Integer> rdd_entry: tuple_pairs) {
            Cassandra_help.UploadRow(cassandra_session, rdd_entry);
        }
    }

    /**
     * Convert from Cassandra ResultSet To List and remove extra symbols
     * @return List<String> of input Data
     */
    public static List<String> FromCassandraToList(ResultSet input_data){
        log.info("Parse input data");
        List<String> input_data_list = new ArrayList<>();
        while (!input_data.isExhausted()) {
            input_data_list.add(input_data.one().toString().replaceAll("[a-zA-Z\\[\\],]", ""));
        }
        return input_data_list;
    }

    /**
     * Create SparkSession func
     * @return SparkSession
     */
    public static SparkSession CreateSession(){
        log.info("Create Spark Session");
        SparkSession sc = SparkSession
                .builder()
                .master("local[6]")
                .appName("LABA_2")
                .getOrCreate();
        return sc;
    }

    /**
     * Core reduce function
     * @param sc - spark Session
     * @param input_data_list - input data to reduce
     * @return - JavaPairRDD
     */
    public static List<Tuple2<String, Integer>> Reduce(SparkSession sc,
                                                    List<String> input_data_list,
                                                    ArrayList<String> vocab ){
        /* convert List<string> to JavaRDD */
        log.info("Create JavaRDD");
        // connect with scala class tags
        ClassTag<String> tag = ClassTag$.MODULE$.apply(String.class);
        // convert List to scala Seq
        Seq<String> result_list_seq = JavaConverters.asScalaIteratorConverter(input_data_list.iterator())
                .asScala().toSeq();
        // convert Seq to JavaRDD
        JavaRDD<String> rdd = sc.sparkContext().parallelize(result_list_seq, 1, tag).toJavaRDD();

        // remove extra fields and convert type
        log.info("Job: Filter JavaRDD");
        JavaRDD<String> rdd_filter = rdd
                .map(x -> x.split(" ")[0] + ",'" + vocab.get(Integer.parseInt(x.split(" ")[3]) - 1) + "'");

        // distinct for number of types
        // rdd_filter = rdd_filter.distinct();

        // create pairs key:entry
        log.info("Job: Create JavaPairRDD");
        JavaPairRDD<String, Integer> rdd_pairs = rdd_filter
                .mapToPair(x -> new Tuple2<String, Integer>((String) x, 1));

        // sum
        log.info("Job: Reduce JavaPairRD");
        return rdd_pairs.reduceByKey(Integer::sum).collect();
    }
}
