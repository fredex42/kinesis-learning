import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class TestRecordProcessorFactory implements IRecordProcessorFactory {
    public TestRecordProcessorFactory() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public IRecordProcessor createProcessor() {
        return new TestRecordProcessor();
    }
}
