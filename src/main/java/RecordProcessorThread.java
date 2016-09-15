import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecordProcessorThread extends Thread {
    public static final Logger logger= LogManager.getLogger("RecordProcessorThread");
    String streamName;
    String regionName;

    public RecordProcessorThread(String streamName,String region)
    {
        super();
        this.streamName = streamName;
        this.regionName = region;
    }

    public void run() {
        /* start up record processors - http://docs.aws.amazon.com/streams/latest/dev/kinesis-record-processor-implementation-app-java.html#kcl-java-interface-v2 */
        final AWSCredentialsProviderChain credentialsProvider = new DefaultAWSCredentialsProviderChain();

        final KinesisClientLibConfiguration config = new KinesisClientLibConfiguration(
                "KinesisTest",
                streamName,
                credentialsProvider,
                "worker-1"
        ).withRegionName(regionName);

        final IRecordProcessorFactory recordProcessorFactory = new TestRecordProcessorFactory();
        final Worker worker = new Worker.Builder()
                .recordProcessorFactory(recordProcessorFactory)
                .config(config)
                .build();

        //try {
            worker.run();
//        } catch(InterruptedException e){
//            logger.info("terminating");
//        }
    }
}
