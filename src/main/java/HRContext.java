import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class HRContext implements Executor {

	private final java.util.concurrent.ExecutorService ex;

	HRContext(int runningThreads) {
		ex = new java.util.concurrent.ThreadPoolExecutor( runningThreads, runningThreads, 100L, java.util.concurrent.TimeUnit.MILLISECONDS, new java.util.concurrent.LinkedBlockingQueue(), new CustomThreadFactory(), new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy() );
	}

	@Override
	public void execute(Runnable runnable) {
		ex.execute( runnable );
	}

	public void end() {
		ex.shutdown();
		try {
			ex.awaitTermination( 20, java.util.concurrent.TimeUnit.SECONDS );
		}
		catch (InterruptedException e) {
			throw new RuntimeException( e );
		}
		ex.shutdownNow();
	}

	private static class CustomThreadFactory implements ThreadFactory {
		java.util.concurrent.atomic.AtomicInteger i = new java.util.concurrent.atomic.AtomicInteger();
		@Override
		public Thread newThread(Runnable runnable) {
			final HRContext.CustomThread customThread = new HRContext.CustomThread( runnable );
			customThread.setName( "C-" + i.incrementAndGet());
			return customThread;
		}
	}

	private static class CustomThread extends Thread {

		public CustomThread(Runnable runnable) {
			super(runnable);
		}
	}

	public static void isRunningOnValidContext() {
		final Thread thread = Thread.currentThread();
		if ( !(thread instanceof  CustomThread)) {
			throw new IllegalStateException("Not running on valid thread! Thread name: " + thread.getName());
		}
	}

}
