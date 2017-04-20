/**
 * Clase que es un simple bucle que crea enteros para emular dfatos en streaming con kafka
 **/
package com.miguel.kafka;

import java.util.Properties;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer09;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import org.apache.flink.streaming.util.serialization.SerializationSchema;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

public class KafkaProducer {

	public static void main(String[] args) throws Exception {
		
		
		//path de donde se encuentra el fichero de propiedades
		String propertiesFile = System.getProperty("user.dir") + "/config/producer.properties";
		ParameterTool parameterTool = ParameterTool.fromPropertiesFile(propertiesFile);
		
		//propiedades
		Properties properties = new Properties();
		properties=parameterTool.getProperties();
		
		//entorno de ejecucion
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<String> stream = env.addSource(new SimpleStringGenerator());
//		stream.addSink(new FlinkKafkaProducer09<String>(
//		        parameterTool.getRequired("bootstrap.servers"),            // broker list
//		        parameterTool.getRequired("topic") ,                  // target topic
//		        new SimpleStringSchema()));
		
		stream.addSink(new FlinkKafkaProducer09<String>(propertiesFile, new SimpleStringSchema(), properties));
		

		env.execute();
	}

	public static class SimpleStringGenerator implements SourceFunction<String> {
		private static final long serialVersionUID = 2174904787118597072L;
		boolean running = true;
		long i = 0;

		@Override
		public void run(SourceContext<String> ctx) throws Exception {
			while (running) {
				ctx.collect("element-" + (i++));
				Thread.sleep(10);
			}
		}

		@Override
		public void cancel() {
			running = false;
		}
	}

	public static class SimpleStringSchema implements DeserializationSchema<String>, SerializationSchema<String>{
		private static final long serialVersionUID = 1L;

		public SimpleStringSchema() {
		}

		public String deserialize(byte[] message) {
			return new String(message);
		}

		public boolean isEndOfStream(String nextElement) {
			return false;
		}

		public byte[] serialize(String element) {
			return element.getBytes();
		}

		public TypeInformation<String> getProducedType() {
			return TypeExtractor.getForClass(String.class);
		}
	}

}
