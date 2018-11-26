import java.util.ArrayList;
import java.util.List;

public class PathFinder {
    private List<Node> openList = new ArrayList<>();
    private List<Node> closedList = new ArrayList<>();
    private List<Node> path = new ArrayList<>();
    private String hMethod;
    private Node start;
    private Node end;

    public PathFinder(Node start, Node end, String hMethod) {
        this.start = start;
        this.end = end;
        this.hMethod = hMethod;
    }

    public void aStar(boolean a[][]) {

        //First, the move cost is 0
        start.setMoveCost(0);

        //Set heuristic from start node to end node using the heuristic method
        setHeuristic(start, start.getX(), start.getY(), end.getX(), end.getY(), hMethod);

        //Start node is added to the chosen node list
        closedList.add(start);

        try {
            //First the surrounding nodes of the start node will be visited
            findNeighbours(a, start);

            //This loop will exit when the goalfoundexception is thrown
            while (true) {
                Node node = openList.get(0); //This is the list of visited nodes, but not the ones chosen as parent
                for (Node n : openList) {
                    //A* because the heuristic is also compared. If heuristic is removed it becomes dijkstra
                    if ((node.getMoveCost() + node.getHeuristic()) > (n.getMoveCost() + n.getHeuristic())) {
                        node = n; //The node with the lowest movecost + heuristic is chosen as the parent node for the next iteration
                    }
                }

                //The chosen node is removed from the visited list and added to the list of chosen nodes
                openList.remove(node);
                closedList.add(node);

                //The neighbours of the chosen node is visited again
                try {
                    findNeighbours(a, node);
                } catch (GoalFoundException e) {
                    throw e;
                }
            }

        } catch (GoalFoundException e) {
        }
    }

    /**
     * This method will call the calculateCost method in all the eight directions of the chosen node
     *
     * @param a    - boolean array
     * @param node - the parent node
     * @throws GoalFoundException
     */
    private void findNeighbours(boolean[][] a, Node node) throws GoalFoundException {
        int x = node.getX();
        int y = node.getY();

        calculateCost(x, y - 1, a, node);   // down
        calculateCost(x, y + 1, a, node);   // up
        calculateCost(x - 1, y, a, node);   //left
        calculateCost(x + 1, y, a, node);   //right

        calculateCost(x - 1, y - 1, a, node);
        calculateCost(x + 1, y - 1, a, node);
        calculateCost(x + 1, y + 1, a, node);
        calculateCost(x - 1, y + 1, a, node);
    }


    public void drawpath(boolean a[][]) {

        //This is to show the nodes that has been visited
        for (Node n : openList) {
            StdDraw.setPenColor(StdDraw.GREEN);
            StdDraw.filledSquare(n.getX(), a.length - n.getY() - 1, .5);
        }

        path.add(end); //The end node is added to the list path

        Node n = closedList.get(closedList.size() - 1); //This is also the end node, but this is added separately to avoid any alterations to the end node
        path.add(n);

        /*
         *While the start node is found, the Node n's parent is added to the list path
         *The parent nodes will always lead to the start node by the shortest path, because we chose the parent by A* algorithm
         */
        while (true) {
            n = n.getParent();
            path.add(n);
            if (n.equals(start)) break;
        }

        /**
         * The path nodes are colored in pink
         */
        for (Node node : path) {
            StdDraw.setPenColor(StdDraw.PINK);
            StdDraw.filledSquare(node.getX(), a.length - node.getY() - 1, .5);
        }

        /**
         * A line is drawn in blue along the path
         */
        for (Node node : path) {
            StdDraw.setPenColor(StdDraw.BLUE);
            try {
                StdDraw.line(node.getParent().getX(), a.length - node.getParent().getY() - 1, node.getX(), a.length - node.getY() - 1);
            } catch (Exception e) {
            }

        }

        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        StdDraw.filledCircle(start.getX(), a.length - start.getY() - 1, .5);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledCircle(end.getX(), a.length - end.getY() - 1, .5);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(end.getX(), a.length - end.getY() - 1, "B");
        StdDraw.text(start.getX(), a.length - start.getY() - 1, "A");
    }

    /**
     * @param x      - x coordinate of the node to be visited
     * @param y      - y coordinate of the node to be visited
     * @param a      - the original boolean array
     * @param parent - the parent node
     * @throws GoalFoundException - the exception thrown when the end node is found
     */
    public void calculateCost(int x, int y, boolean[][] a, Node parent) throws GoalFoundException {
        int N = a.length;
        //validates if the node coordinates are out of bounds, or cannot be visited
        if (x < 0 || x >= N) return;
        if (y < 0 || y >= N) return;
        if (!a[y][x]) return;

        Node n = new Node(y, x);//makes a new node from the coordinates
        double gValue = 0.0;//this is for the cost from parent node to current node

        //sets the move cost according to the heuristic method
        switch (hMethod) {
            case "M":
                gValue = manhattanDistance(parent.getX(), parent.getY(), x, y);
                break;

            case "E":
                gValue = euclideanDistance(parent.getX(), parent.getY(), x, y);
                break;

            case "C":
                gValue = chebyshevDistance(parent.getX(), parent.getY(), x, y);
                break;
        }

        //Checks if the visited node is the end node
        if (n.equals(end)) {
            end.setParent(parent); //sets the parent node as the parent
            end.setMoveCost(gValue + parent.getMoveCost()); //set the end node move cost by adding the parents move cost as well
            throw new GoalFoundException("Goal Found"); //Throws the custom exception to indicate that the end node has been found
        }

        /**
         * This checks if the current node has previously been visited or chosen as a parent node
         * If not, it will set the heuristic to the end node and add the total move cost and set the parent
         * Then finally it will be added to the visited node list
         */
        if (!(openList.contains(n) || closedList.contains(n))) {
            setHeuristic(n, x, y, end.getX(), end.getY(), hMethod);
            n.setParent(parent);
            n.setMoveCost(gValue + parent.getMoveCost());
            openList.add(n);
        }

    }

    /**
     * This method will set the heuristic to the passed node, by the x and y values which are also passed as parameters
     *
     * @param node - The node which the heuristic is to be set
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param heu  - the heuristic method
     */
    private void setHeuristic(Node node, int x1, int y1, int x2, int y2, String heu) {
        switch (heu) {
            case "M":
                node.setHeuristic(manhattanDistance(x1, y1, x2, y2));
                break;

            case "E":
                node.setHeuristic(euclideanDistance(x1, y1, x2, y2));
                break;

            case "C":
                node.setHeuristic(chebyshevDistance(x1, y1, x2, y2));
                break;
        }
    }

    private int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }


    private double euclideanDistance(int x1, int y1, int x2, int y2) {
        return Math.round((Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2))) * 10) / 10.0;
    }

    private int chebyshevDistance(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
}
