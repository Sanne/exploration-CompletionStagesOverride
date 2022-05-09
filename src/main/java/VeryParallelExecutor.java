import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class VeryParallelExecutor implements java.util.concurrent.Executor  {

	private final ExecutorService ex;

	VeryParallelExecutor(int taskNumber) {
		ex = new ThreadPoolExecutor( taskNumber, taskNumber, 10, java.util.concurrent.TimeUnit.MINUTES, new java.util.concurrent.LinkedBlockingQueue() );
	}

	@Override
	public void execute(Runnable runnable) {
		ex.execute( runnable );
	}

	void shutdownAndWait() {
		ex.shutdown();
		try {
			ex.awaitTermination( 10, java.util.concurrent.TimeUnit.MINUTES );
		}
		catch (InterruptedException e) {
			throw new RuntimeException( e );
		}
		ex.shutdownNow();
	}
}
