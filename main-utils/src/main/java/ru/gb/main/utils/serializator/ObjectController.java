package ru.gb.main.utils.serializator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ru.gb.main.utils.logging.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ObjectController<T extends Serializable> implements Closeable {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();
    private final OutputStream outputStream;
    private final InputStream inputStream;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final Logger logger;

    public ObjectController(InputStream in, OutputStream out, Logger logger) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = new BufferedWriter(new OutputStreamWriter(out));
        this.inputStream = in;
        this.outputStream = out;
        this.logger = logger;
    }

    public Optional<T> readBin() {
        Optional<T> optional = Optional.empty();
        try {
            ObjectInputStream in = new ObjectInputStream(inputStream);
            optional = Optional.of((T) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            logger.log(e.getMessage());
        }
        return optional;
    }

    public Optional<T> readJson(Class<T> clazz) {
        Optional<T> optional = Optional.empty();
        try {
            String json = in.readLine();
            optional = Optional.of(objectMapper.readValue(json, clazz));
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
        return optional;
    }

    public Optional<T> readXml(Class<T> clazz) {
        Optional<T> optional = Optional.empty();
        try {
            String xml = in.readLine();
            optional = Optional.of(xmlMapper.readValue(xml, clazz));
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
        return optional;
    }

    public boolean sendBin(T object) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(object);
            return true;
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
        return false;
    }

    public boolean sendJson(T object) {
        try {
            String json = objectMapper.writeValueAsString(object) + "\n";
            out.write(json);
            out.flush();
            return true;
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
        return false;
    }

    public boolean sendXml(T object) {
        try {
            String xml = xmlMapper.writeValueAsString(object) + "\n";
            out.write(xml);
            out.flush();
            return true;
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        out.close();
        in.close();
    }
}
