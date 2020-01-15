import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Threading implements Runnable, ActionListener {
    /** Color that indicates that the side is obstructed: java.awt.Color[r=126,g=173,b=79] and java.awt.Color[r=145,g=196,b=96] */
    private final static Color right_color = new Color(126, 173, 79);
    private final static Color left_color = new Color(145, 196, 96);

    public Threading() {
        running = true;
        isPaused = false;
    }

    volatile boolean running;
    volatile boolean isPaused;

    @Override
    public void run() {
        char side = 'l';
        while (running) {
            while (!isPaused) {
                Thread.onSpinWait();
                BufferedImage left = null;
                BufferedImage right = null;
                try {
                    Robot robot = new Robot();
                    left = robot.createScreenCapture(new Rectangle(1320, 550, 20, 80));
                    right = robot.createScreenCapture(new Rectangle(1520, 550, 20, 80));

                    boolean left_obstructed = false;
                    boolean right_obstructed = false;

                    if (side == 'l') {
                        assert left != null;
                        Color c = new Color(left.getRGB(0,0));
                        System.out.println(c.toString());
                        left_obstructed = isObstructed(convertTo2DUsingGetRGB(left));
                    } else {
                        assert right != null;
                        Color c = new Color(right.getRGB(0,0));
                        right_obstructed = isObstructed(convertTo2DUsingGetRGB(right));
                        System.out.println(c.toString());
                    }

                    if (right_obstructed) {
                        System.out.println("right side obstructed");
                        side = 'l';
                        robot.keyPress(KeyEvent.VK_LEFT);
                    } else if (left_obstructed) {
                        System.out.println("left side obstructed");
                        side = 'r';
                        robot.keyPress(KeyEvent.VK_RIGHT);
                    } else {
                        System.out.println("no obstruction");
                        if (side == 'l') { robot.keyPress(KeyEvent.VK_LEFT); }
                        else { robot.keyPress(KeyEvent.VK_RIGHT); }
                    }

                } catch (AWTException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(30);
                } catch(InterruptedException e) {
                    return;
                }
            }
        }

    }


    /** From Stackoverflow
     * https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
     * */
    private static int[][] convertTo2DUsingGetRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getRGB(col, row);
            }
        }
        return result;
    }

    public boolean isObstructed(int[][] arr) {
        for (int[] a : arr) {
            for (int i : a) {
                Color c = new Color(i);
                if (c.equals(right_color) || c.equals(left_color)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Thread t = new Thread(new Threading());
        t.start();


    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }
}
