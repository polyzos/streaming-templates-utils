import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;


public class Runner {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment
                .createLocalEnvironmentWithWebUI(new Configuration());

        CsvReaderFormat<ClickEvent> csvFormat = CsvReaderFormat.forPojo(ClickEvent.class);
        FileSource<ClickEvent> source =
                FileSource.forRecordStreamFormat(
                        csvFormat, Path.fromLocalFile(
                                new File("events.csv"))
                        )
                        .build();

        final DataStreamSource<ClickEvent> file =
                env.fromSource(source, WatermarkStrategy.noWatermarks(), "File Source");

        final FileSink<String> sink = FileSink
                .forRowFormat(new Path("test.csv"), new SimpleStringEncoder<String>("UTF-8"))
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(Duration.ofMinutes(15))
                                .withInactivityInterval(Duration.ofMinutes(5))
                                .withMaxPartSize(MemorySize.ofMebiBytes(1024))
                                .build())
                .build();

        file
                .keyBy(ClickEvent::getUserSession)
                .map(e -> {
                    return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                            e.getEventTime(),
                            e.getEventType(),
                            e.getProductId(),
                            e.getCategoryId(),
                            e.getCategoryCode(),
                            e.getBrand(),
                            e.getPrice(),
                            e.getUserid(),
                            e.getUserSession()
                            );
                }).sinkTo(sink);

        env.execute("Preprocessing Test");
    }
}
