package fsm.modules;

import fsm.Messages;
import fsm.States;
import internal.Module;

public class MySecondModule extends Module {

    private final String name;

    public MySecondModule(String name) {
        this.name = String.format("[%s]",name);
    }

    @Override
    public void onStarted() {
        System.out.println("MySecondModule "+name+" has been started!");
    }

    @Override
    public void onMessageReceived(Messages message) {}

    @Override
    public void onStateChanged(States state) throws InterruptedException {
        if (state == States.STATE_B) {
            System.out.println("State is B");
            Thread.sleep(1000);
            switchGlobalState(States.STATE_A);
        }
    }
}
