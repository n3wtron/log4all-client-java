package net.log4all.client;

import java.util.Date;
import java.util.UUID;

import net.log4all.client.exceptions.Log4AllException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by igor on 03/06/14.
 */
public class AddTest {
	private String trash = "";
	
	@BeforeClass
	public void createTrash(){
		StringBuffer trashBuff = new StringBuffer();
		for (int i=0;i<50;i++){
			trashBuff.append(UUID.randomUUID().toString()+"\n");
		}
		trash = trashBuff.toString();
	}
	
    @Test(threadPoolSize = 10,invocationCount = 1)
    public void addLog() throws Log4AllException {
        Log4AllClient cl = new Log4AllClient("http://localhost:9000","test");
        cl.log("Test da client java #client:java #rand:"+ UUID.randomUUID().toString(),"INFO");
    }
    
    @Test(threadPoolSize = 10,invocationCount = 1)
    public void addLogWithStack() throws Log4AllException {
        Log4AllClient cl = new Log4AllClient("http://localhost:9000","test");
        cl.log("Test da client java con ##stack #client:java #rand:"+ UUID.randomUUID().toString(),"INFO",trash);
    }
    
    
    @Test(threadPoolSize = 10,invocationCount = 10, dependsOnMethods="addLog")
    public void addLogGrouped() throws Log4AllException, JSONException {
        Log4AllClient cl = new Log4AllClient("http://localhost:9000","test");
        JSONArray logs = new JSONArray();
        for (int i=0;i<100;i++){
        	logs.put(Log4AllClient.toJSON("Test da client java #client:java #rand:"+ UUID.randomUUID().toString(),"INFO",trash,new Date()));
        }
        cl.log(logs);
    }
}
