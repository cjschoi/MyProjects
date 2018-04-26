package test.nio2.util;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.evanlennick.retry4j.AsyncCallExecutor;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import com.evanlennick.retry4j.exception.RetriesExhaustedException;
import com.evanlennick.retry4j.exception.Retry4jException;

public class RetryUtil {
	
	enum Constants {
		RETRY_MAX_NUMBER(3); // retryÃÖ´ë È½¼ö
		
		int value;
		private Constants(int value) {
			this.value = value;
		}
		private int getValue() {
			return this.value;
		}
	}
	
	private static RetryConfig retryOnAnyExceptionConfig = new RetryConfigBuilder()
            .retryOnAnyException()
            .withFixedBackoff()
            .withMaxNumberOfTries(Constants.RETRY_MAX_NUMBER.getValue())
            .withDelayBetweenTries(Duration.ofMillis(250))
            .build();
	
	public static <V> V runWithRetry(Callable<V> callable, int...retryMaxNumberr ) 
				throws Exception {
        try {
        	if (retryMaxNumberr.length > 0) {
        		retryOnAnyExceptionConfig.setMaxNumberOfTries(retryMaxNumberr[0]);
        	}
        	
            AsyncCallExecutor<V> executor = new AsyncCallExecutor<>(retryOnAnyExceptionConfig);
            executor.onSuccess(s -> System.out.println("Success!"))
            		.onCompletion(s -> System.out.println("Retry execution complete!"))
            		.onFailure(s -> System.out.println("Failed! All retries exhausted..."))
            		.afterFailedTry(s -> System.out.println(s.getTotalTries() + " Try failed! Will try again in 0ms."))
            		//.beforeNextTry(s -> System.out.println("Trying again..."))
            		;
            
            CompletableFuture<Status<V>> future = executor.execute(callable);
        	Status<V> status = future.get();
        	System.out.println("status.wasSuccessful(): "+status.wasSuccessful());

        	if(!status.wasSuccessful()) { 
        		throw new RetriesExhaustedException("Retry failed"
        				, status.getLastExceptionThatCausedRetry(), status);
        	}
        	return status.getResult();
        } catch (InterruptedException | ExecutionException | RetriesExhaustedException me) {
        	//RetriesExhaustedException => the call exhausted all tries without succeeding
			me.printStackTrace();
			
			if(me instanceof RetriesExhaustedException) {
				Status status = ((RetriesExhaustedException) me).getStatus();
		        System.out.println(status.wasSuccessful());
		        System.out.println(status.getCallName());
		        System.out.println(status.getTotalElapsedDuration());
		        System.out.println(status.getTotalTries());
		        System.out.println(status.getLastExceptionThatCausedRetry());
			}
			throw me;
        } catch(Throwable te) {
        	te.printStackTrace();
			throw new Retry4jException("Retry failed", te);
		}
	}
}
