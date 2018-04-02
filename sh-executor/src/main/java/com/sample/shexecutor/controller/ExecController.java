package com.sample.shexecutor.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ExecController {
//	@Value("${conf}")
//	private Map<String, Object> conf;

	@Value("${info.bin:.}")
	private String bin = "./";

	private ObjectMapper mapper = new ObjectMapper();

	@RequestMapping("exec/{cmd}")
	public Object exec(@PathVariable String cmd, String filename) throws ExecuteException, IOException{
		/*
		 * https://stackoverflow.com/questions/6295866/how-can-i-capture-the-output-of-a-command-as-a-string-with-commons-exec
		 * https://commons.apache.org/proper/commons-exec/tutorial.html
		 * 
		 * http://d2.naver.com/helloworld/1113548
		 * http://www.baeldung.com/run-shell-command-in-java
		 */
		
		String command = this.bin + cmd;

		// not found
		File shell = new File(command);
		if(!shell.exists()){
			return "There is a no such file.";
		}

		// invalid path
		File bin = new File(this.bin);
		if(!bin.getAbsolutePath().equals(shell.getParentFile().getAbsolutePath())){
			return "Invalid path.";
		}
		
		// run script
	    CommandLine commandline = CommandLine.parse(command);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

	    DefaultExecutor exec = new DefaultExecutor();
	    exec.setStreamHandler(streamHandler);
	    exec.execute(commandline);

	    // response console output as sting
	    if(StringUtils.isEmpty(filename))
	    	return outputStream.toString("UTF-8");

	    // response console output as file
	    HttpHeaders respHeaders = new HttpHeaders();
	    respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    respHeaders.setContentLength(outputStream.size());
	    respHeaders.setContentDispositionFormData("attachment", filename);

	    return new ResponseEntity<byte[]>(outputStream.toByteArray(), respHeaders, HttpStatus.OK);
	}

	@RequestMapping("exec/info")
	public String path() throws ExecuteException, IOException{
		Map<String, Object> info = new HashMap<>();
		
		info.put("bin", new File(bin).getAbsolutePath().replace('\\', '/'));

	    return mapper.writeValueAsString(info);
	}
}
