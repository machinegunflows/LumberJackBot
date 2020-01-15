import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class NoThreading {

    /** Color that indicates that the side is obstructed: java.awt.Color[r=126,g=173,b=79] and java.awt.Color[r=145,g=196,b=96] */
    private final static Color right_color = new Color(126, 173, 79);
    private final static Color left_color = new Color(145, 196, 96);

    public static void main(String[] args) throws AWTException, IOException {

//        Robot robot = new Robot();
//        BufferedImage left = robot.createScreenCapture(new Rectangle(1320, 550, 20, 80));
//        BufferedImage right = robot.createScreenCapture(new Rectangle(1520, 550, 20, 80));
//
//        File outputfile = new File("left.png");
//        ImageIO.write(left, "png", outputfile);
//        outputfile = new File("right.png");
//        ImageIO.write(right, "png", outputfile);
//
//        System.out.println(new Color(left.getRGB(0,0)));


        char side = 'l';
        while (true) {
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
                        left_obstructed = isObstructed(convertTo2DUsingGetRGB(left));
                    } else {
                        assert right != null;
                        Color c = new Color(right.getRGB(0,0));
                        right_obstructed = isObstructed(convertTo2DUsingGetRGB(right));
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

    public static boolean isObstructed(int[][] arr) {
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

}
