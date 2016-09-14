//package com.theguardian.test.kinesistest;
import com.amazonaws.services.kinesis.model.StreamDescription;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.CreateStreamRequest;
import com.amazonaws.services.kinesis.model.CreateStreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    public static final Logger logger= LogManager.getLogger("KinesisTest.Main");

    static final int threads=1;
    static final String streamName="KinesisTestStream";

    public String createTestStream(AmazonKinesis conn) {
        logger.info("Creating test stream with name " + streamName);

        CreateStreamRequest request = new CreateStreamRequest()
                .withStreamName(streamName)
                .withShardCount(threads);

        CreateStreamResult result = conn.createStream(request);
        StreamDescription desc=new StreamDescription();

        do {
            desc=conn.describeStream(streamName).getStreamDescription();
            logger.info("Stream state is " + desc.getStreamStatus());
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e){
                logger.error("sleep was interrupted");
            }
        } while(desc.getStreamStatus() != "ACTIVE");
        return "";
    };

    public static void main(String[] args) {
        // write your code here
        AmazonKinesis conn=AmazonKinesisClientBuilder.defaultClient();
        RecordProcessorThread threadList[] = new RecordProcessorThread[threads];

        logger.info("Starting up " + threads + " consumer threads...");
        for(int t=0;t<threads;t++){
            RecordProcessorThread thread = new RecordProcessorThread(streamName);
            threadList[t] = thread;
            //thread.start();
        }

        logger.info("Thread startup done.");

        System.out.println("Hello World!");
    }
}
