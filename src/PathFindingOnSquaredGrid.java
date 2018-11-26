import java.util.Scanner;

public class PathFindingOnSquaredGrid {

    // test client
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.println("Enter size of the grid");

        int gridSize = in.nextInt(); //Get the size of the grid

        boolean[][] randomlyGenMatrix = random(gridSize, 0.8); //create a boolean array according to the size of the grid

        StdArrayIO.print(randomlyGenMatrix);
        show(randomlyGenMatrix, true);

        System.out.println();
        System.out.println("The system percolates: " + percolates(randomlyGenMatrix));

        System.out.println();
        System.out.println("The system percolates directly: " + percolatesDirect(randomlyGenMatrix));
        System.out.println();

        int Ay, Ax, By, Bx;

        do {
            //Get the starting and ending x and y coordinates from the user
            System.out.println("Enter y for A > ");
            Ay = in.nextInt();

            System.out.println("Enter x for A > ");
            Ax = in.nextInt();

            System.out.println("Enter y for B > ");
            By = in.nextInt();

            System.out.println("Enter x for B > ");
            Bx = in.nextInt();

            //Check if the user entered coordinates are valid
            try{
                if(randomlyGenMatrix[Ay][Ax] && randomlyGenMatrix[By][Bx]){
                    break;
                }else{
                    System.out.println("A node is unreachable. Enter again");
                }
            }catch (ArrayIndexOutOfBoundsException e){
                System.out.println("A node is unreachable. Enter again");
            }
        }while(true);

        String hMethod;
        //Get the heuristic method from the user
        do {
            System.out.println("Enter heuristic method (M/E/C)");
            hMethod = in.next().toUpperCase();

            if((hMethod.equals("M")) || (hMethod.equals("E")) || (hMethod.equals("C")))break;
        }while(true);

        //Create the start and end node objects
        Node start = new Node();
        start.setY(Ay);
        start.setX(Ax);

        Node end = new Node();
        end.setY(By);
        end.setX(Bx);

        try {
            PathFinder pFinder = new PathFinder(start, end, hMethod); //Create a pathfinder object by passing the array and start and end nodes

            Stopwatch aStar = new Stopwatch(); //Create a stopwatch instance to check the time elapsed

            pFinder.aStar(randomlyGenMatrix); //Call the aStar method to start searching the shortest path
            StdOut.println("Elapsed time  aStar's Search= " + aStar.elapsedTime());

            pFinder.drawpath(randomlyGenMatrix); //Draw the shortest path as well as the searched nodes

            System.out.println("Count " + end.getMoveCost()); //The end node will have the final total move cost

        } catch (IndexOutOfBoundsException e) {
            System.out.println("The system does not percolates start point to end point");
        }

    }

    // given an N-by-N matrix of open cells, return an N-by-N matrix
    // of cells reachable from the top
    public static boolean[][] flow(boolean[][] open) {
        int N = open.length;

        boolean[][] full = new boolean[N][N];
        flow(open, full, 0, 0);

        return full;
    }

    // determine set of open/blocked cells using depth first search
    public static void flow(boolean[][] open, boolean[][] full, int i, int j) {
        int N = open.length;

        // base cases
        if (i < 0 || i >= N) return;    // invalid row
        if (j < 0 || j >= N) return;    // invalid column
        if (!open[i][j]) return;        // not an open cell
        if (full[i][j]) return;         // already marked as open

        full[i][j] = true;

        flow(open, full, i + 1, j);   // down
        flow(open, full, i, j + 1);   // right
        flow(open, full, i, j - 1);   // left
        flow(open, full, i - 1, j);   // up
    }

    // does the system percolate?
    public static boolean percolates(boolean[][] open) {
        int N = open.length;

        boolean[][] full = flow(open);
        for (int j = 0; j < N; j++) {
            if (full[N - 1][j]) return true;
        }

        return false;
    }

    // does the system percolate vertically in a direct way?
    public static boolean percolatesDirect(boolean[][] open) {
        int N = open.length;

        boolean[][] full = flow(open);
        int directPerc = 0;
        for (int j = 0; j < N; j++) {
            if (full[N - 1][j]) {
                // StdOut.println("Hello");
                directPerc = 1;
                int rowabove = N - 2;
                for (int i = rowabove; i >= 0; i--) {
                    if (full[i][j]) {
                        // StdOut.println("i: " + i + " j: " + j + " " + full[i][j]);
                        directPerc++;
                    } else break;
                }
            }
        }

        // StdOut.println("Direct Percolation is: " + directPerc);
        if (directPerc == N) return true;
        else return false;
    }

    // draw the N-by-N boolean matrix to standard draw
    public static void show(boolean[][] a, boolean which) {
        int N = a.length;
        StdDraw.setXscale(-1, N);
        StdDraw.setYscale(-1, N);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (a[i][j] == which)
                    StdDraw.square(j, N - i - 1, .5);
                else StdDraw.filledSquare(j, N - i - 1, .5);
    }

    // draw the N-by-N boolean matrix to standard draw, including the points A (x1, y1) and B (x2,y2) to be marked by a circle
    public static void show(boolean[][] a, boolean which, int x1, int y1, int x2, int y2) {
        int N = a.length;
        StdDraw.setXscale(-1, N);
        ;
        StdDraw.setYscale(-1, N);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (a[i][j] == which)
                    if ((i == x1 && j == y1) || (i == x2 && j == y2)) {
                        StdDraw.circle(j, N - i - 1, .5);
                    } else StdDraw.square(j, N - i - 1, .5);
                else StdDraw.filledSquare(j, N - i - 1, .5);
    }

    // return a random N-by-N boolean matrix, where each entry is
    // true with probability p
    public static boolean[][] random(int N, double p) {
        boolean[][] a = new boolean[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                a[i][j] = StdRandom.bernoulli(p);
        return a;
    }
}


