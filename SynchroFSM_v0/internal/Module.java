package internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import fsm.Messages;
import fsm.States;

public abstract class Module {
    private final AtomicBoolean isStateHandlerRunning = new AtomicBoolean(false);
    private final AtomicBoolean isMessageHandlerRunning = new AtomicBoolean(false);
    private final AtomicBoolean isStartHandlerRunning = new AtomicBoolean(false);

    // Dedicated ExecutorService for one-off threads
    private ExecutorService stateHandlerThreadPool = Executors.newCachedThreadPool();
    private ExecutorService messageHandlerThreadPool = Executors.newCachedThreadPool();
    private ExecutorService startHandlerThreadPool = Executors.newCachedThreadPool();

    // List to track running tasks
    private final List<Future<?>> runningTasks = new ArrayList<>();

    public Module() {
        ModuleRegistry.register(this);
    }

    public void broadcastMessage(Messages message) {
        FiniteStateMachine.getInstance().broadcastMessage(message);
    }

    public void switchGlobalState(States state) {
        FiniteStateMachine.getInstance().switchState(state);
    }

    public abstract void onMessageReceived(Messages message) throws InterruptedException;

    public abstract void onStateChanged(States state) throws InterruptedException;

    public abstract void onStarted() throws InterruptedException;

    private synchronized void trackTask(Future<?> task) {
        runningTasks.add(task);
    }

    private synchronized void cancelRunningTasks() throws InterruptedException {
        for (Future<?> task : runningTasks) {
            task.cancel(true); // Attempt to cancel running tasks
        }
        runningTasks.clear(); // Clear the task list
    }

    void onMessageReceivedAsync(Messages message) {
        if (isMessageHandlerRunning.compareAndSet(false, true)) {
            Future<?> task = messageHandlerThreadPool.submit(() -> {
                try {
                    onMessageReceived(message);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    // Terminate thread
                    Thread.currentThread().interrupt();
                    messageHandlerThreadPool.shutdown();
                    messageHandlerThreadPool = Executors.newCachedThreadPool();
                    // Mark handler as no longer running
                    isMessageHandlerRunning.set(false);
                }
            });
            trackTask(task);
        }
    }

    void onStateChangedAsync(States state) {
        if (isStateHandlerRunning.compareAndSet(false, true)) {
            Future<?> task = stateHandlerThreadPool.submit(() -> {
                try {
                    onStateChanged(state);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    // Terminate thread
                    Thread.currentThread().interrupt();
                    stateHandlerThreadPool.shutdown();
                    stateHandlerThreadPool = Executors.newCachedThreadPool();
                    // Mark handler as no longer running
                    isStateHandlerRunning.set(false);
                }
            });
            trackTask(task);
        }
    }

    void onStartedAsync() {
        if (isStartHandlerRunning.compareAndSet(false, true)) {
            stateHandlerThreadPool = Executors.newCachedThreadPool();
            messageHandlerThreadPool = Executors.newCachedThreadPool();
            startHandlerThreadPool = Executors.newCachedThreadPool();
            Future<?> task = startHandlerThreadPool.submit(() -> {
                try {
                    onStarted();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    // Terminate thread
                    Thread.currentThread().interrupt();
                    startHandlerThreadPool.shutdown();
                    startHandlerThreadPool = Executors.newCachedThreadPool();
                    // Mark handler as no longer running
                    isStartHandlerRunning.set(false);
                }
            });
            trackTask(task);
        }
    }

    void onStoppedAsync() {
        try {
            // Cancel all ongoing tasks
            cancelRunningTasks();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // Shut down the thread pools after all tasks are completed
            stateHandlerThreadPool.shutdownNow();
            messageHandlerThreadPool.shutdownNow();
            startHandlerThreadPool.shutdownNow();
        }
    }

    protected double getElapsedTime() {
        return FiniteStateMachine.getInstance().getElapsedTime();
    }

    protected States getCurrentState() {
        return FiniteStateMachine.getInstance().getCurrentState();
    }

}

