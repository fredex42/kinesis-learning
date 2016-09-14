import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;

public class RecordProcessorThread extends Thread {
    String streamName;
    public RecordProcessorThread(String streamName)
    {
        super();
        this.streamName = streamName;
    }

    public void run() {
        /* start up record processors - http://docs.aws.amazon.com/streams/latest/dev/kinesis-record-processor-implementation-app-java.html#kcl-java-interface-v2 */
        final AWSCredentialsProviderChain credentialsProvider = new DefaultAWSCredentialsProviderChain();

        final KinesisClientLibConfiguration config = new KinesisClientLibConfiguration(
                "KinesisTest",
                streamName,
                credentialsProvider,
                "worker-1"
        );

        final IRecordProcessorFactory recordProcessorFactory = new TestRecordProcessorFactory();
        final Worker worker = new Worker.Builder()
                .recordProcessorFactory(recordProcessorFactory)
                .config(config)
                .build();

        worker.run();
    }
}
