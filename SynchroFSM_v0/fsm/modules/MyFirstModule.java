package fsm.modules;

import java.util.concurrent.atomic.AtomicInteger;

import fsm.Messages;
import fsm.States;
import internal.FiniteStateMachine;
import internal.Module;

public class MyFirstModule extends Module {

    private final String name;
    private AtomicInteger count = new AtomicInteger(0);

    public MyFirstModule(String name) {
        this.name = String.format("[%s]",name);
    }

    @Override
    public void onStarted() throws InterruptedException {
        System.out.println("MyFirstModule "+name+" has been started!");
        switchGlobalState(States.STATE_B);
    }

    @Override
    public void onMessageReceived(Messages message) {
        if (message == Messages.GREET) {
            System.out.println(name+" Greeting count: " + count.incrementAndGet());
        }
    }

    @Override
    public void onStateChanged(States state) throws InterruptedException {
        if (state == States.STATE_A) {
            // Wait a second and switch the state to B
            System.out.println(name+" State is A");
            Thread.sleep(1000);
            switchGlobalState(States.STATE_B);
        }
    }

}
