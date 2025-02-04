import fsm.Messages;
import fsm.modules.MyFirstModule;
import fsm.modules.MySecondModule;
import internal.FiniteStateMachine;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // "Modules" will respond to state changes
//        MyFirstModule myFirstModule = new MyFirstModule("Module A");
//        MySecondModule mySecondModule = new MySecondModule("Module B");
//
//        FiniteStateMachine.getInstance().start();
//
//        Thread.sleep(500);
//
//        for (int i = 0; i < 1000; i++) {
//            FiniteStateMachine.getInstance().broadcastMessage(Messages.GREET);
//            Thread.sleep(500);
//        }

        double disp = 3;
        double v0 = -6;
        double vm = 6;
        double am1 = 6;
        double am2 = 1.5;

        DynamicMotionProfile1D mp = new DynamicMotionProfile1D(
                disp,
                0,
                v0,
                vm,
                am1,
                am2
        );

        double duration = mp.getDuration();
        double dt = 0.12;
        System.out.println("duration: "+duration);

        double negativeBuffer = 8*vm;
        for (double t = 0; t < duration+1; t += dt) {
            double pos = mp.getDisplacement(t);
            double vel = mp.getVelocity(t);
            double acc = mp.getAcceleration(t);

            double pv = pos;
            int hashtags = (int)Math.round(8*pv);

            for (int i = 0; i < negativeBuffer - Math.abs(Math.min(0,hashtags)); i++) {
                System.out.print(" ");
            }
            for (int i = 0; i < Math.abs(Math.min(0,hashtags)); i++) {
                System.out.print("#");
            }
            System.out.print("|");
            for (int i = 0; i < Math.max(0,hashtags); i++) {
                System.out.print("#");
            }
            System.out.print(" "+(Math.round(pv*100)/100d));
            System.out.println();
        }

    }
}
