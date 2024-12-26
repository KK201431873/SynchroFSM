import fsm.Messages;
import fsm.modules.MyFirstModule;
import fsm.modules.MySecondModule;
import internal.FiniteStateMachine;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // "Modules" will respond to state changes
        MyFirstModule myFirstModule = new MyFirstModule("Module A");
        MySecondModule mySecondModule = new MySecondModule("Module B");

        FiniteStateMachine.getInstance().start();

        Thread.sleep(500);

        for (int i = 0; i < 1000; i++) {
            FiniteStateMachine.getInstance().broadcastMessage(Messages.GREET);
            Thread.sleep(500);
        }

    }
}
