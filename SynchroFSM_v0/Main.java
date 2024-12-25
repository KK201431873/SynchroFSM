import fsm.modules.MyFirstModule;
import fsm.modules.MySecondModule;
import internal.FiniteStateMachine;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MyFirstModule myFirstModule = new MyFirstModule("Module A");
        MySecondModule mySecondModule = new MySecondModule("Module B");

        FiniteStateMachine.getInstance().start();


    }
}
