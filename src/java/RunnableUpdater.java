import java.util.concurrent.TimeUnit;

public class RunnableUpdater implements Runnable{
    private final GameFrame frame;
    private static final int FRAMES_PER_SECOND=60;
    public RunnableUpdater(GameFrame frame) {
        this.frame = frame;
    }

    @Override
    public void run() {
        while (!frame.end){
            frame.move();
            try {
                frame.checkforEnd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            frame.appleEaten();
            try {
                TimeUnit.MILLISECONDS.sleep(1000/FRAMES_PER_SECOND);
            } catch (InterruptedException ignored){}
        }
    }
}
