/**
 * Clase que te cuenta las palabras de datos metidos en Streaming usado como tutorial
 * 
 * pagina adictos al trabajo:https://www.adictosaltrabajo.com/tutoriales/introduccion-a-apache-flink/
 * pagina oficial:https://ci.apache.org/projects/flink/flink-docs-release-0.8/streaming_guide.html
 * 
 * primero se abre un socket por la terminal con la siguiente instruccion: nc -lk 9999
 * y despues ya se ejecuta
 **/
package flink;

import java.io.File;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class SrtreamingWordCount {

	public static void main(String[] args) throws Exception {

		// traza para saber la rtuta del proyecto
		// System.out.println(System.getProperty("user.dir"));

		// host por donde entran los datos
		String host = "localhost";
		// puerto por donde llegan los datos
		int port = 9999;
		// ruta donde se craeara el fichero con la salida(no usado de momento)
		String path = System.getProperty("user.dir") + "/resultado";
		
		//creo los ficheros y le doy permisos donde voy a guardar el resultado
		File filetxt = new File(path+"/texto.txt");
		filetxt.setReadable(true, false);
		filetxt.setWritable(true,false);
		
		
		

		// instanciamos el entorno de ejecución
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// obtenemos el stream de datos del host provisto
		DataStream<String> text = env.socketTextStream(host, port);

		DataStream<Tuple2<String, Integer>> dataStream = text
                .flatMap(new Splitter())
                .keyBy(0)
                .sum(1);
        
		// para guardarlo en fichero el texto txt
        dataStream.writeAsText(path+"/texto.txt",WriteMode.OVERWRITE );
        
        
		//para imprimir el texto por pantalla
        dataStream.print();
        
        
        // ejecutar el programa
        env.execute("Socket Stream WordCount");
    }
	
    /**
     * Clase anonima para separar por palabras el texto introducido
     **/
    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {

        /**
		 * atributo para serializar  
		 */
		private static final long serialVersionUID = 1L;

		public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word: sentence.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }

}
