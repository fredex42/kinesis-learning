import com.amazonaws.services.kinesis.model.*;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    public static final Logger logger= LogManager.getLogger("KinesisTest.Main");

    static final int threads=1;
    static final String streamName="KinesisTestStream";

    public static StreamDescription waitForState(AmazonKinesis conn,String waitStreamName,String desiredState,boolean throwOnError)
    {
        while(true){
            StreamDescription desc=conn.describeStream(waitStreamName).getStreamDescription();
            logger.info("Stream state is " + desc.getStreamStatus());
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                logger.error("sleep was interrupted");
            }
            if(desc.getStreamStatus().equals(desiredState)) return desc;
            if(! desc.getStreamStatus().equals("CREATING") && ! desc.getStreamStatus().equals("ACTIVE")){
                if(throwOnError) throw new RuntimeException("Stream creation failed with status " + desc.getStreamStatus());
                return null;
            }
        }
    }

    public static String createTestStream(AmazonKinesis conn) {
        logger.info("Creating test stream with name " + streamName);

//        CreateStreamRequest request = new CreateStreamRequest()
//                .withStreamName(streamName)
//                .withShardCount(threads);

        try {
            CreateStreamResult result = conn.createStream(streamName,threads);
        } catch(com.amazonaws.services.kinesis.model.ResourceInUseException e){
            //test stream already exists
            logger.info("Test stream already exists");
        }

        final StreamDescription desc = waitForState(conn,streamName,"ACTIVE",true);
        return desc.getStreamName();
    };

    public static void deleteTestStream(AmazonKinesis conn, String streamName) {
        logger.info("Deleting stream named " + streamName);
        DeleteStreamRequest rq = new DeleteStreamRequest()
                .withStreamName(streamName);
        DeleteStreamResult result = conn.deleteStream(rq);
        logger.info(result.toString());
    }

    public static void main(String[] args) {
        AmazonKinesis conn=AmazonKinesisClientBuilder.defaultClient();
        final String createdStreamName = createTestStream(conn);
        //final String createdStreamName = streamName;

        RecordProcessorThread threadList[] = new RecordProcessorThread[threads];

        logger.info("Starting up " + threads + " consumer threads...");
        for(int t=0;t<threads;t++){
            RecordProcessorThread thread = new RecordProcessorThread(createdStreamName);
            threadList[t] = thread;
            //thread.start();
        }

        logger.info("Thread startup done.");

        deleteTestStream(conn, createdStreamName);
    }
}
