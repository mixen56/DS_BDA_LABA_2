package LABA_2;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.SparkSession;
import java.util.List;
import java.util.ArrayList;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

import scala.Tuple2;

/**
 * Подсчет количества типов взаимодействия с новостью.
 */
@Slf4j
public class LABA_2_APP {

    /**
     * @param args - args[0] - справочник типов взаимодействия
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("Usage: java -jar SparkRDDApplication.jar vocabulary.file");
        }

        log.info("Appliction started!");

        // Create Spark Session
        SparkSession sc = LABA_2_CORE.CreateSession();

        // Get vocabulary for types
        ArrayList<String> vocab = Catalog.read(args[0]);

        // Cassandra init and load data
        Session cassandra_session = Cassandra_help.CreateSession();
        ResultSet input_data = Cassandra_help.ReadData(cassandra_session);

        // convert data from Cassandra to List
        List<String> input_data_list = LABA_2_CORE.FromCassandraToList(input_data);

        /* MAIN SPARK REDUCE */
        List<Tuple2<String, Integer>> tuple_pairs = LABA_2_CORE.Reduce(sc, input_data_list, vocab);

        /* Save Result */
        Cassandra_help.CreateResultTable(cassandra_session);
        LABA_2_CORE.Upload(tuple_pairs, cassandra_session);

        // Close cassandra session
        Cassandra_help.CloseSession(cassandra_session);

        log.info("Appliction finished!");
    }
}
