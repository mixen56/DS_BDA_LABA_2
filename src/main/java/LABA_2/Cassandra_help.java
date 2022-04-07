package LABA_2;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import lombok.extern.slf4j.Slf4j;
import scala.Tuple2;

/**
 * Cassandra help functions
 */
@Slf4j
public class Cassandra_help {
    static String FromTable = new String("LABA_2.DATA");
    static String ToTable = new String("LABA_2.RESULT");

    /**
     * Create Cassandra Session
     * @return Cassandra Session object
     */
    public static Session CreateSession() {
        log.info("Create Cassandra Session");
        // Creating Cluster object
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        return cluster.connect();
    }

    /**
     * Create Load data from Cassandra
     * @return Cassandra ResultSet
     */
    public static ResultSet ReadData(Session session){
        log.info("Read data from Cassandra");
        String query = "SELECT * FROM " + FromTable;
        return session.execute(query);
    }

    /**
     * Create Result Table in Cassandra
     */
    public static void CreateResultTable(Session session){
        // create result table
        log.info("Create Result Table");
        StringBuilder sb_create = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(ToTable)
                .append(" (news_id int,")
                .append("type text,")
                .append("sum int,")
                .append("PRIMARY KEY (news_id, type));");
        String create_query = sb_create.toString();
        session.execute(create_query);
    }

    /**
     * Load Row to Cassandra Table
     * @param session - Cassandra Session
     * @param rdd_entry - Tuple, which should be uploaded to table
     */
    public static void UploadRow(Session session, Tuple2<String, Integer> rdd_entry){
        // System.out.println(rdd_entry); // Debug
        StringBuilder sb_save = new StringBuilder("INSERT INTO ")
                .append(ToTable).append(" (news_id, type, sum) ")
                .append("VALUES ").append(rdd_entry.toString())
                .append(";");
        String save_query = sb_save.toString();
        session.execute(save_query);
    }

    public static void CloseSession(Session session){
        session.close();
        session.getCluster().close();
    }
}
