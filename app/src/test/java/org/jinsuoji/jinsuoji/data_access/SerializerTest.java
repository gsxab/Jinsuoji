package org.jinsuoji.jinsuoji.data_access;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertFalse;

public class SerializerTest {
    private JsonFactory factory;

    @Before
    public void jsonFactory() {
        factory = new JsonFactory();
    }

    @Test
    public void makeExampleSerialized() {
        try {
            File file = new File("F:/jinsuoji.txt");
            JsonGenerator generator = factory.createGenerator(new FileOutputStream(file));
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(generator, Serializer.DBMirror.loadExample());
            assertFalse(false);
        } catch (IOException e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
}