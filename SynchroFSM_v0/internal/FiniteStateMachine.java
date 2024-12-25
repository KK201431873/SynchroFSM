package internal;

import fsm.Messages;
import fsm.States;

public class FiniteStateMachine {
    private static FiniteStateMachine instance;
    private States currentState;
    private boolean running = false;
    private double startTime = 0;

    private FiniteStateMachine() {
        this.currentState = States.STATE_A; // Default initial state
    }

    public static synchronized FiniteStateMachine getInstance() {
        if (instance == null) {
            instance = new FiniteStateMachine();
        }
        return instance;
    }

    public synchronized void switchState(States newState) {
        if (currentState != newState) {
            this.currentState = newState;
            notifyModulesOfStateChange(newState);
        }
    }

    public synchronized void broadcastMessage(Messages message) {
        notifyModulesOfMessage(message);
    }

    public synchronized void start() {
        if (running) {
            throw new IllegalStateException("The FiniteStateMachine is already running.");
        }
        running = true;
        notifyModulesOfStart();
        this.startTime = System.currentTimeMillis()/1000d;
    }

    public synchronized double getElapsedTime() {
        double currentTime = System.currentTimeMillis()/1000d;
        return currentTime-startTime;
    }

    public synchronized void stop() {
        if (!running) {
            throw new IllegalStateException("The FiniteStateMachine is not running.");
        }
        running = false;

        // Notify all modules of stop
        notifyModulesOfStop();
    }

    private void notifyModulesOfStateChange(States newState) {
        ModuleRegistry.getModules().forEach(module ->
                module.onStateChangedAsync(newState));
    }

    private void notifyModulesOfMessage(Messages message) {
        ModuleRegistry.getModules().forEach(module ->
                module.onMessageReceivedAsync(message));
    }

    private void notifyModulesOfStart() {
        ModuleRegistry.getModules().forEach(Module::onStartedAsync);
    }

    private synchronized void notifyModulesOfStop() {
        ModuleRegistry.getModules().forEach(Module::onStoppedAsync);
    }

    public synchronized States getCurrentState() {
        return currentState;
    }

    public synchronized boolean isRunning() {
        return running;
    }
}


