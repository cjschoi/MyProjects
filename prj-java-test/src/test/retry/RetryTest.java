package test.retry;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.evanlennick.retry4j.AsyncCallExecutor;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import com.evanlennick.retry4j.exception.RetriesExhaustedException;

public class RetryTest {

	public static void main(String[] args)  {
		
		RetryConfig retryOnAnyExceptionConfig = new RetryConfigBuilder()
	                .retryOnAnyException()
	                .withFixedBackoff()
	                .withMaxNumberOfTries(3)
	                .withDelayBetweenTries(Duration.ofMillis(250))
	                .build();
		
        Callable<Boolean> callable = () -> {
        	System.out.println("callable..");
        	return true;
        	//throw new RuntimeException(); 
        };

        try {
            AsyncCallExecutor<Boolean> executor = new AsyncCallExecutor<>(retryOnAnyExceptionConfig);
            executor.onSuccess(s -> System.out.println("Success!"))
            		.onCompletion(s -> System.out.println("Retry execution complete!"))
            		.onFailure(s -> System.out.println("Failed! All retries exhausted..."))
            		.afterFailedTry(s -> System.out.println(s.getTotalTries() + " Try failed! Will try again in 0ms."))
            		//.beforeNextTry(s -> System.out.println("Trying again..."))
            		;
            
            CompletableFuture<Status<Boolean>> future = executor.execute(callable);
        	Status<Boolean> status = future.get();
        	System.out.println("status.wasSuccessful(): "+status.wasSuccessful());

        	if(!status.wasSuccessful()) { 
        		throw new RetriesExhaustedException("Retry failed"
        				, status.getLastExceptionThatCausedRetry(), status); // case1
        	}
		} catch (InterruptedException | ExecutionException | RetriesExhaustedException e) {
        	System.out.println("Exception 1111");
        	//RetriesExhaustedException => the call exhausted all tries without succeeding
        	e.printStackTrace();
			
			if(e instanceof RetriesExhaustedException) {
				Status status = ((RetriesExhaustedException) e).getStatus();
		        System.out.println(status.wasSuccessful());
		        System.out.println(status.getCallName());
		        System.out.println(status.getTotalElapsedDuration());
		        System.out.println(status.getTotalTries());
		        System.out.println(status.getLastExceptionThatCausedRetry());
			}
        } catch(Throwable ree) {
        	System.out.println("Exception 2222");
			ree.printStackTrace();
			System.exit(-1);
		}
        	
	} 	
}
