import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    private Picture picture;
    private double[][] energy;

    public SeamCarver(Picture picture)                // create a seam carver object based on the given picture
    {
        if(picture == null) throw new IllegalArgumentException("picture cannot be null!");
        this.picture = new Picture(picture);
        this.energy = new double[height()][width()];
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                this.energy[i][j] = energy(j, i);
            }
        }
    }
    public Picture picture()                          // current picture
    {
        return new Picture(this.picture);
    }
    public int width()                            // width of current picture
    {
        return this.picture.width();
    }
    public int height()                           // height of current picture
    {
        return this.picture.height();
    }
    public double energy(int x, int y)               // energy of pixel at column x and row y
    {
        if(x < 0 || x >= width() || y < 0 || y >= height())
            throw new IllegalArgumentException("invalid input range!");
        if(x == 0 || x == width() - 1 || y == 0 || y == height() - 1) return 1000;
        int leftRGB = this.picture.getRGB(x-1, y);
        int rightRGB = this.picture.getRGB(x+1, y);
        int upRGB = this.picture.getRGB(x, y-1);
        int downRGB = this.picture.getRGB(x, y+1);
        int R = ((leftRGB >> 16) & 0xFF) - ((rightRGB >> 16) & 0xFF);
        int G = ((leftRGB >> 8) & 0xFF) - ((rightRGB >> 8) & 0xFF);
        int B = (leftRGB & 0xFF) - (rightRGB & 0xFF);
        double deltaXsquare = R*R + G*G + B*B;

        R = ((upRGB >> 16) & 0xFF) - ((downRGB >> 16) & 0xFF);
        G = ((upRGB >> 8) & 0xFF) - ((downRGB >> 8) & 0xFF);
        B = (upRGB & 0xFF) - (downRGB & 0xFF);
        double deltaYsquare = R*R + G*G + B*B;
        return Math.sqrt(deltaXsquare + deltaYsquare);

    }
    public int[] findHorizontalSeam()               // sequence of indices for horizontal seam
    {
        return findVerticalSeam(transpose());
    }
    public int[] findVerticalSeam()                 // sequence of indices for vertical seam
    {
        return findVerticalSeam(arrayCopy());
    }
    public void removeHorizontalSeam(int[] seam)   // remove horizontal seam from current picture
    {
        validateSeam(seam, false);
        Picture newPic = new Picture(width(), height() - 1);
        for (int j = 0; j < seam.length; j++) {
            for (int i = 0; i < seam[j]; i++) {
                newPic.set(j, i, this.picture.get(j, i));
            }
            for (int i = seam[j]; i < height() - 1; i++) {
                this.energy[i][j] = this.energy[i+1][j];
                newPic.set(j, i, this.picture.get(j, i+1));
            }
        }
        this.picture = newPic;
        for (int i = 1; i < seam.length - 1; i++) {
            if(seam[i] > 0)
                this.energy[seam[i] - 1][i] = energy(i, seam[i] - 1);
            if(seam[i] < height())
                this.energy[seam[i]][i] = energy(i, seam[i]);

        }
    }
    public void removeVerticalSeam(int[] seam)     // remove vertical seam from current picture
    {
        validateSeam(seam, true);
        Picture newPic = new Picture(width() - 1, height());
        for (int i = 0; i < seam.length; i++) {
            for (int j = 0; j < seam[i]; j++) {
                newPic.set(j, i, this.picture.get(j, i));
            }
            for (int j = seam[i]; j < width() - 1; j++) {
                this.energy[i][j] = this.energy[i][j+1];
                newPic.set(j, i, this.picture.get(j+1, i));
            }
        }
        this.picture = newPic;
        for (int i = 1; i < seam.length -1 ; i++) {
            if(seam[i] > 0)
                this.energy[i][seam[i] - 1] = energy(seam[i] - 1, i);
            if(seam[i] < width())
                this.energy[i][seam[i]] = energy(seam[i], i);
        }

    }

    private double[][] arrayCopy() {
        double[][] copy = new double[height()][width()];
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                copy[i][j] = this.energy[i][j];
            }
        }
        return copy;
    }

    private double[][] transpose() {
        double[][] copy = new double[width()][height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                copy[i][j] = this.energy[j][i];
            }
        }
        return copy;
    }

    private int[] findVerticalSeam(double[][] copy) {
        StdOut.println(copy.length + " " + copy[0].length);

        int[][] edgeTo = new int[copy.length][copy[0].length];
        double lowestEnergy = Integer.MAX_VALUE;
        int lowestColumn = 0;
        for (int i = 1; i < copy.length; i++) {
            for (int j = 0; j < copy[0].length; j++) {
                if(j == 0 || j == copy[0].length - 1) {
                    copy[i][j] += copy[i-1][j];
                    continue;
                }
                if(copy[i-1][j] <= copy[i-1][j-1] && copy[i-1][j] <= copy[i-1][j+1]) {
                    copy[i][j] += copy[i-1][j];
                    edgeTo[i][j] = j;
                } else if(copy[i-1][j-1] <= copy[i-1][j] && copy[i-1][j-1] <= copy[i-1][j+1]) {
                    copy[i][j] += copy[i-1][j-1];
                    edgeTo[i][j] = j - 1;
                } else {
                    copy[i][j] += copy[i-1][j+1];
                    edgeTo[i][j] = j + 1;
                }
                if(i == copy.length - 1) {
                    if(copy[i][j] < lowestEnergy) {
                        lowestEnergy = copy[i][j];
                        lowestColumn = j;
                    }
                }
            }
        }
        int[] verticalPath = new int[copy.length];
        verticalPath[copy.length-1] = lowestColumn;
        for (int i = copy.length - 2; i >= 0 ; i--) {
            verticalPath[i] = edgeTo[i+1][verticalPath[i+1]];
        }

        return verticalPath;
    }

    private void validateSeam(int[] seam, boolean isVertical) {
        if(seam == null) throw new IllegalArgumentException("Sean cannot be null!");
        if(isVertical) {
            if(seam.length != height()) throw new IllegalArgumentException("Vertical seam of wrong length!");
            if(width() <= 1) throw new IllegalArgumentException("Width <= 1!");
            for (int i = 0; i < seam.length; i++) {
                if(seam[i] < 0 || seam[i] >= width())
                    throw new IllegalArgumentException("Vertical seam of entry outside prescribed range!");
                if(i != 0 && Math.abs(seam[i] - seam[i-1]) > 1)
                    throw new IllegalArgumentException("Vertical seam of two adjacent entries differ by more than 1!");
            }
        } else {
            if(seam.length != width()) throw new IllegalArgumentException("Horizontal seam of wrong length!");
            if(height() <= 1) throw new IllegalArgumentException("height <= 1!");
            for (int i = 0; i < seam.length; i++) {
                if(seam[i] < 0 || seam[i] >= height())
                    throw new IllegalArgumentException("Horizontal seam of entry outside prescribed range!");
                if(i != 0 && Math.abs(seam[i] - seam[i-1]) > 1)
                    throw new IllegalArgumentException("Horizontal seam of two adjacent entries differ by more than 1!");
            }
        }
    }


}