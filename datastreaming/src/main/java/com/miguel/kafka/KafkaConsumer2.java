package com.miguel.kafka;

import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;

import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

public class KafkaConsumer2 {

	public static void main(String[] args) throws Exception {
		

		// path de donde se encuentra el fichero de propiedades
		String propertiesFile = System.getProperty("user.dir") + "/config/producer.properties";
		ParameterTool parameterTool = ParameterTool.fromPropertiesFile(propertiesFile);

		// propiedades
		Properties properties = new Properties();
		properties = parameterTool.getProperties();
		
		//topic
		String topic="twitter";
		
		// entorno de ejecucion
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		
		DataStream<String> messageStream = env.addSource(new FlinkKafkaConsumer09<String>(
				topic, new SimpleStringSchema(),properties));

		// print() will write the contents of the stream to the TaskManager's
		// standard out stream
		// the rebelance call is causing a repartitioning of the data so that
		// all machines
		// see the messages (for example in cases when "num kafka partitions" <
		// "num flink operators"
		messageStream.rebalance().map(new MapFunction<String, String>() {
			private static final long serialVersionUID = -6867736771747690202L;

			public String map(String value) throws Exception {
				return "Kafka and Flink says: " + value;
			}
		}).print();

		env.execute();
	}

}
