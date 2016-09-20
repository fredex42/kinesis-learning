import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.util.Base64;

public class MyRecord {
    private JsonNode jsondata;
    private String textdata;

    public MyRecord(ByteBuffer buffer) throws java.io.IOException{
        ByteBuffer decoded_bytes=Base64.getDecoder().decode(buffer);
        textdata = decoded_bytes.toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory(); // since 2.1 use mapper.getFactory() instead
        JsonParser jp = factory.createJsonParser(decoded_bytes.toString());
        jsondata = mapper.readTree(jp);
    }

    public String toString(){
        return textdata;
    }
}
