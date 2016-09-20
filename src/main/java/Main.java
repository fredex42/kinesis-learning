import com.amazonaws.services.kinesis.model.*;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    public static final Logger logger= LogManager.getLogger("Main");

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
            if(! desc.getStreamStatus().endsWith("ING")){
                if(throwOnError) throw new RuntimeException("Stream creation failed with status " + desc.getStreamStatus());
                return null;
            }
        }
    }

    public static String createTestStream(AmazonKinesis conn) {
        logger.info("Creating test stream with name " + streamName);

        try {
            CreateStreamResult result = conn.createStream(streamName,threads);
        } catch(com.amazonaws.services.kinesis.model.ResourceInUseException e){
            //test stream already exists
            logger.info("Test stream already exists");
        }

        final StreamDescription desc = waitForState(conn,streamName,"ACTIVE",true);
        if(desc!=null) return desc.getStreamName();
        return "";  //this will not happen as waitForState will not return null while throwOnError is true
    };

    public static void deleteTestStream(AmazonKinesis conn, String streamName) {
        logger.info("Deleting stream named " + streamName);
        DeleteStreamRequest rq = new DeleteStreamRequest()
                .withStreamName(streamName);
        DeleteStreamResult result = conn.deleteStream(rq);
        logger.info(result.toString());
    }

    public static void main(String[] args) {
        final AmazonKinesis conn=AmazonKinesisClientBuilder.defaultClient();
        final String createdStreamName = createTestStream(conn);

        final RecordProcessorThread threadList[] = new RecordProcessorThread[threads];

        logger.info("Starting up " + threads + " consumer threads...");
        for(int t=0;t<threads;t++){
            RecordProcessorThread thread = new RecordProcessorThread(createdStreamName,"eu-west-1");
            threadList[t] = thread;
            thread.start();
        }

        logger.info("Thread startup done.");

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                shutdown(conn,createdStreamName,threadList);
            }
        });

        try {
            Thread.sleep(5000);
        } catch(InterruptedException e){
            logger.warn("timeout interrupted");
        }

    }

    public static void shutdown(AmazonKinesis conn, String createdStreamName, RecordProcessorThread threadList[]) {
        logger.info("Shutting down threads...");
        for(int t=0;t<threads;t++){
            threadList[t].shutdown();
        }

        logger.info("Waiting for termination...");
        for(int t=0;t<threads;t++){
            try {
                threadList[t].join(10000);
            } catch(InterruptedException e){
                logger.warn("attempting to join thread " + t + " was interrupted");
            }
        }

        logger.info("Deleting test stream...");
        deleteTestStream(conn, createdStreamName);
        try {
            waitForState(conn, createdStreamName, "DELETED", true);
        } catch(com.amazonaws.services.kinesis.model.ResourceNotFoundException e){ //this will be thrown when the stream goes away and we try to check its status
            logger.info("Stream deleted");
        }
    }
}
