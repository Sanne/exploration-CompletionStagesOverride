import java.util.concurrent.Executor;

public final class OneOffDelegatingExecutor implements Executor {
	private final Executor hrContext;
	private Runnable deferredTask;

	OneOffDelegatingExecutor(Executor hrContext){
		this.hrContext = hrContext;
	}

	@Override
	public void execute(Runnable runnable) {
		this.deferredTask = runnable;
	}

	public void runHeldTasks() {
		if ( this.deferredTask != null )
			this.hrContext.execute( this.deferredTask );
		else {
			throw new IllegalStateException( "... ");
		}
	}
}
