import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestRecordProcessor implements IRecordProcessor {
    public static final Logger logger= LogManager.getLogger("TestRecordProcessor");

    public void initialize(InitializationInput initializationInput) {
        logger.info("Initialising TestRecordProcessor");
    }

    public void processRecords(ProcessRecordsInput processRecordsInput) {
        logger.info("In processRecords");

    }

    public void shutdown(ShutdownInput shutdownInput) {
        logger.info("Shutting down TestRecordProcessor");
    }
}
