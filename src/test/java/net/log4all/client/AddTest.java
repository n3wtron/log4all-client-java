package net.log4all.client;

import junit.framework.Assert;
import net.log4all.client.Log4AllClient;
import net.log4all.client.exceptions.Log4AllException;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * Created by igor on 03/06/14.
 */
public class AddTest {
    @Test(threadPoolSize = 10,invocationCount = 1000)
    public void addLog() throws Log4AllException {
        Log4AllClient cl = new Log4AllClient("http://localhost:6543");
        Assert.assertTrue(cl.log("Test da client java #client:java #rand:"+ UUID.randomUUID().toString()));
    }
}
