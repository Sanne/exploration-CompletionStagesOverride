import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Experiments with the twisted semantics of {@link CompletableFuture#supplyAsync(Supplier)}
 */
public class OtherApp {

	//Switch this constant to experiment with different behaviours
	private static final boolean wait = true;

	public static void main(String[] args) {
		System.out.println("Method 'main' is running on thread " + threadName());
		VeryParallelExecutor executor = new VeryParallelExecutor( 4 );

//		OneOffDelegatingExecutor taskControl = new OneOffDelegatingExecutor( executor );

		CompletableFuture comp = CompletableFuture.supplyAsync(
				OtherApp::asyncGenerateInteger,
				executor )
				.thenApply( OtherApp::incrementInput )//Which thread is running this? : timing dependent!
				.thenAccept( OtherApp::printOutput );

//		taskControl.runHeldTasks();

		comp.join();

		executor.shutdownAndWait();
	}

	private static String threadName() {
		return '['+Thread.currentThread().getName()+']';
	}

	private static Integer asyncGenerateInteger() {
		if (wait) sleep2seconds();
		System.out.println( "asyncGenerateInteger is running on: " + threadName() );
		return 1;
	}

	private static void sleep2seconds() {
		try {
			Thread.sleep( 2000 );
		}
		catch (InterruptedException ignored) {
			ignored.printStackTrace();
		}
	}

	private static Integer incrementInput(Integer i) {
		System.out.println( "incrementInput running on: " + threadName() );
		return i + 1;
	}

	private static void printOutput(Integer i) {
		System.out.println( "printOutput running on: " + threadName() + "; result is: " + i );
	}
}
