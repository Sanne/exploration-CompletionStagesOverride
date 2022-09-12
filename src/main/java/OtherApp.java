import java.util.concurrent.CompletableFuture;

public class OtherApp {

	private static final boolean wait = false;

	public static void main(String[] args) {
		VeryParallelExecutor executor = new VeryParallelExecutor( 4 );

//		OneOffDelegatingExecutor taskControl = new OneOffDelegatingExecutor( executor );

		CompletableFuture comp = CompletableFuture.supplyAsync(
				OtherApp::asyncGenerateInteger,
				executor )
				.thenApply( OtherApp::incrementInput )
				.thenAccept( OtherApp::printOutput );

//		taskControl.runHeldTasks();

		comp.join();

		executor.shutdownAndWait();
	}
	private static Integer asyncGenerateInteger() {
		if (wait) sleep2seconds();
		System.out.println( "asyncGenerateInteger: " + Thread.currentThread().getName() );
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
		System.out.println( "incrementInput: " + Thread.currentThread().getName() );
		return i + 1;
	}

	private static void printOutput(Integer i) {
		System.out.println( "printOutput: " + Thread.currentThread().getName() );
		System.out.println( "result is: " + i );
	}
}
