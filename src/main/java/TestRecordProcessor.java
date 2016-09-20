import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class TestRecordProcessor implements IRecordProcessor {
    public static final Logger logger= LogManager.getLogger("TestRecordProcessor");

    public void initialize(InitializationInput initializationInput) {
        logger.info("Initialising TestRecordProcessor");
    }

    public void processRecords(ProcessRecordsInput processRecordsInput) {
        logger.info("In processRecords");
        for(Record rec: processRecordsInput.getRecords()){
            logger.info("Got record:");
            logger.info(rec.toString());
            logger.info("Record number: " + rec.getSequenceNumber());
            logger.info("Partition key: " + rec.getPartitionKey());
            logger.info("Approx timestamp: " + rec.getApproximateArrivalTimestamp());
            logger.info("Hash code: " + rec.hashCode());
            logger.info("Data: " + rec.getData());

            try {
                MyRecord recdata = new MyRecord(rec.getData());
                logger.info("Record contents: " + recdata.toString());
            } catch(IOException e){
                logger.error("An io error occurred: " + e);
            }
        }
        logger.info("Completed processRecords");
    }

    public void shutdown(ShutdownInput shutdownInput) {
        logger.info("Shutting down TestRecordProcessor");
    }
}
