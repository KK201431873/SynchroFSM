package fsm.modules;

import fsm.Messages;
import fsm.States;
import internal.Module;

public class MyFirstModule extends Module {

    private final String name;

    public MyFirstModule(String name) {
        this.name = String.format("[%s]",name);
    }

    @Override
    public void onStarted() {
        System.out.println("MyFirstModule "+name+" has been started!");
        switchGlobalState(States.STATE_B);
    }

    @Override
    public void onMessageReceived(Messages message) {}

    @Override
    public void onStateChanged(States state) throws InterruptedException {
        if (state == States.STATE_A) {
            System.out.println("State is A");
            Thread.sleep(1000);
            switchGlobalState(States.STATE_B);
        }
    }

}
