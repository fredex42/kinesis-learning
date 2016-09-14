import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;

public class TestRecordProcessor implements IRecordProcessor {
    public void initialize(InitializationInput initializationInput) {

    }

    public void processRecords(ProcessRecordsInput processRecordsInput) {

    }

    public void shutdown(ShutdownInput shutdownInput) {

    }
}
