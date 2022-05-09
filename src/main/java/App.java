import java.util.concurrent.CompletionStage;

public class App {
	private static final java.util.concurrent.CompletionStage<Void> VOID = completedFuture( null );
	private static final boolean LOG = true;
	private static final int TASKS = 1000;
	private final HRContext context;
	private final VeryParallelExecutor vpe;
	App(HRContext context, VeryParallelExecutor vpe) {
		this.context = context;
		this.vpe = vpe;
	}

	public static void main(String[] args) {
		HRContext context = new HRContext( 4 );
		VeryParallelExecutor vpe = new VeryParallelExecutor( TASKS );
		App app = new App( context, vpe );
		app.run();
		vpe.shutdownAndWait();
		context.end();
	}

	private void run() {
		for ( int i = 0; i < TASKS; i++ ) {
			vpe.execute( this::runTask );
		}
	}

	private CompletionStage<Object> secondStep() {
		return completedFuture( new App.Second() ).thenCompose( v -> check( "second" ) )
				.thenCompose( v -> waitALittle() );
	}

	private java.util.concurrent.CompletionStage<Object> waitALittle() {
		try {
			Thread.sleep( 50 );
		}
		catch (InterruptedException e) {
			throw new RuntimeException( e );
		}
		return completedFuture( new App.Second() );
	}

	private Object handleErrors(Throwable throwable) {
		throwable.printStackTrace();
		return null;
	}

	private CompletionStage<Object> check(String label) {
		println( "Checking: " + label + "..." );
		HRContext.isRunningOnValidContext();
		println( "... " + label + " Checked." );
		return completedFuture( new App.Second() );
	}

	private void println(String s) {
		if ( LOG ) {
			final String name = Thread.currentThread().getName();
			System.out.println( name + "\t" + s );
		}
	}

	private CompletionStage<Object> firstStep() {
		return completedFuture( new App.First() ).thenCompose( v -> check( "first" ) );
	}

	<T> java.util.concurrent.CompletionStage<T> stage(java.util.function.Function<Void, java.util.concurrent.CompletionStage<T>> stageSupplier) {
		return voidFuture().thenComposeAsync( stageSupplier, context );
	}

	public static <T> java.util.concurrent.CompletionStage<T> completedFuture(T value) {
		return java.util.concurrent.CompletableFuture.completedFuture( value );
	}

	public static java.util.concurrent.CompletionStage<Void> voidFuture() {
		return VOID;
	}

	private void runTask() {
		println( "running task" );
		stage( v -> firstStep() ).thenApply( v -> secondStep() ).toCompletableFuture().join();
	}


	private static class First {
	}

	private static class Second {
	}
}
