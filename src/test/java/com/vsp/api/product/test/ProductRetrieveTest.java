package com.vsp.api.product.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vsp.api.product.ProductApiUtil;

public class ProductRetrieveTest
{
	private Logger logger = LoggerFactory.getLogger(getClass().getName());
	private static String SUCCESS = "Success";
	private static String FAILURE = "Failure";
	private static String NONE = "None";

	private ProductApiUtil apiUtil = new ProductApiUtil();

	private String asOfDate = "2016-12-01";

	int numFileInputLines = 0;
	int numLinesProcessed = 0;
	
	public ProductRetrieveTest() throws ClientProtocolException, IOException, ParseException {
		super();
 	}
	
	private String rerieveProduct(String value) 
	{
		String result = NONE;
		String[] values = value.split("~");
		String key = values[0].trim();
		JSONObject retrieveResult = null;
		if (key != null && key.length() > 0) {
			retrieveResult = apiUtil.retrieve(key, asOfDate);
			
			if (retrieveResult == null)
				result = key + SUCCESS;
			else 
				result = key + FAILURE;
				
		}		
		return result;
	}

	public void execute(String[] args) {

		System.out.println("Start FileSplit ...");
		
		String path = args[0];
		String input_file = args[1];

		try {
			List<String> fileInput = Files.readAllLines(Paths.get(path + "/" + input_file), StandardCharsets.UTF_8);
			numFileInputLines = fileInput.size();
			logger.info("# of file input lines=" + numFileInputLines);

			Long t1 = System.currentTimeMillis();
			
			List<String> results = fileInput.parallelStream()
					.map(string -> rerieveProduct(string))
					.collect(Collectors.toList());
			
			Long t2 = System.currentTimeMillis();        
			Long executionTime = t2 - t1;
			
			logger.info("rerieveProduct took {} ms", executionTime);
			logger.info("average time {} ms", executionTime / numFileInputLines);

			String result_file = path + "/output-retrieve.txt";
			Files.write(Paths.get(result_file), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * sample program arguments: "C:/LocalWAS/rating_files/rollout3" "input.txt"
	 * 
	 * How to change the default Fork/Join pool size for parallel streams? One of the options: change the common Fork/Join pool size using a JVM argument:
	 * -Djava.util.concurrent.ForkJoinPool.common.parallelism=16
	 * 
	 * A sample VM argument: handle large files by increasing the default JVM size 64m of stand-alone java application
	 * -Xms64m -Xmx512m -Djava.util.concurrent.ForkJoinPool.common.parallelism=8
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("\n	Usage: foldname filename \n");
			System.exit(1);
		}
		
		new ProductRetrieveTest().execute(args);
       
		System.exit(0);
	}

}
