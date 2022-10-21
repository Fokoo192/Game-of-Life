import java.util.*;

public class Life{
    private int iter;
    private int dimension;
    private String pattern;
    private PictureDemo picDemo;

    private int[][] prev;
    private int[][] curr;

    public Life(int iter, int dimension, String pattern){
        this.iter = iter;
        this.dimension = dimension;
        this.pattern = pattern;
        picDemo = new PictureDemo(dimension, dimension, 10); //for the GUI
        prev = new int[dimension][dimension];
        curr = new int[dimension][dimension];

        
        initializeGrid(this.pattern);
        for (int i=0; i<this.iter; i++){
            //display current cells
            picDemo.generateGrid(curr);
            picDemo.show();

            //compute the new states
            swapArrays("toPrev");
            computeNewStates();
            try{
            Thread.sleep(300); //adding some delay
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        }

    }
    
    //finding neighbours for a particular cell
    private int[][] generateNeighbours(int x, int y){
        int[][] neighbours = new int[8][2];

        int[] arr = {x,Math.floorMod(y-1,dimension)};   //Math.floorMod is used as we want the grid to wrap around
        neighbours[0] = arr;
        int[] arr1 = {x,Math.floorMod(y+1,dimension)};
        neighbours[1] = arr1;
        int[] arr2 = {Math.floorMod(x-1,dimension), y};
        neighbours[2] = arr2;
        int[] arr3 = {Math.floorMod(x+1,dimension),y};
        neighbours[3] = arr3;
        int[] arr4 = {Math.floorMod(x+1,dimension),Math.floorMod(y-1,dimension)};
        neighbours[4] = arr4;
        int[] arr5 = {Math.floorMod(x-1,dimension),Math.floorMod(y-1,dimension)};
        neighbours[5] = arr5;
        int[] arr6 = {Math.floorMod(x+1,dimension),Math.floorMod(y+1,dimension)};
        neighbours[6] = arr6;
        int[] arr7 = {Math.floorMod(x-1,dimension),Math.floorMod(y+1,dimension)};
        neighbours[7] = arr7;

        return neighbours;
    }
    //finds how many neighbours are alive
    private int numOfAliveNeighbours(int[][] neighbours){
        int aliveCounter = 0;
        for (int i = 0; i < neighbours.length; i++){
            //getting the coordinates of each neighbour
            int x = neighbours[i][0]; 
            int y = neighbours[i][1];

            if (prev[x][y] == 1){aliveCounter++;} //check if the the cell is alive
        }
        return aliveCounter;
    }

    //applies the rule of the game of life
    public void computeNewStates(){
        for (int x = 0; x < dimension; x++){
            for (int y=0; y < dimension; y++){
                int cell = prev[x][y]; //get each cell from the prev iteration 
                int[][] neighbours = this.generateNeighbours(x, y); //get its neighbours
                int aliveNeighbours = this.numOfAliveNeighbours(neighbours); //get the number of live neighbours
                
                //rules of the game of life
                if (cell == 1 && (aliveNeighbours < 2 || aliveNeighbours > 3)){ curr[x][y] = 0;} //cell dies due to underpopulation or overpopulation
                if (cell == 0 && aliveNeighbours == 3) {curr[x][y] = 1;} //cell becomes live if the neighbours are exactly 3
                if (cell == 1 && (aliveNeighbours == 2 || aliveNeighbours ==3)) {curr[x][y] = 1;} //cell survives if the neighbours are 2 or 3
                if (cell == 0 && aliveNeighbours != 3) {curr[x][y] = 0;} //cells remain dead if neighbours are not exactly 3
            }
        }
    }

    public void initializeGrid(String pattern){
        if (pattern.equals("R")) { //random pattern
            Random rand = new Random();
            int[][] cells = new int[dimension][dimension];
            for (int i=0; i<dimension; i++){
                for (int j=0; j<dimension; j++){
                    cells[i][j] = rand.nextInt(2);
                }
            }
            prev = cells;
            this.computeNewStates();
        }

        if (pattern.equals("P")){ //Penta-decathlon Oscillator 
            //try to make it in the middle of the grid 
            this.emptyGrid(prev);
            int x = dimension/2;
            int y = dimension/2;
            //making the pattern
            for (int c=0; c<10; c++){
                if (c==2 || c==7){
                prev[x+c][y-1] = 1;
                prev[x+c][y+1] = 1;
            }
                else prev[x+c][y] = 1;
            }
        }
        if (pattern.equals("S")){ //Simkin glider gun
            this.emptyGrid(prev);
            int x = dimension/2;
            int y = dimension/2;
            //making the pattern
            for (int c=0; c <3; c++){
                prev[x+c][y] = 1;
                if (c==0){
                    prev[x+c][y+1] = 1;
                    prev[x+c][y+2] = 1;
                }
                if (c==2){
                    prev[x+c][y-1] = 1;
                    prev[x+c][y+1] = 1;
                }
            }
            createSquare(x-11, y);
            createSquare(x-18, y);
            createSquare(x-15, y+3);
            createSquare(x+6, y+11);
            createSquare(x+13, y+11);
            createSquare(x+9, y+8);
        }
        //copying the prev array to the current array
        swapArrays("toCurr");

    }
    private void emptyGrid(int[][]array){
        for (int x=0; x< this.dimension; x++){
            for (int y=0; y< this.dimension; y++){
                array[x][y] = 0;
            }
        }
    }
    //creates a sqaure used in Smikin Glider Gun
    private void createSquare(int x, int y){ //(x,y) must be on the bottom left of the square
        prev[x][y] = 1;
        prev[x][y-1] = 1;
        prev[x+1][y-1] = 1;
        prev[x+1][y] = 1;

    } 

    //used to swap arrays 
    private void swapArrays(String type){
        if (type.equals("toCurr")){
            for (int i = 0; i < prev.length; i++) {
                for (int j = 0; j < prev.length; j++) {
                    curr[i][j] = prev[i][j];
                }
            } 
        }
        else if (type.equals("toPrev")){
            for (int i = 0; i < curr.length; i++) {
                for (int j = 0; j < curr.length; j++) {
                    prev[i][j] = curr[i][j];
                }
            }
        }
    }


    public static void main(String[] args) {
        int iter = Integer.parseInt(args[0]);
        int dimension = Integer.parseInt(args[1]);
        String pattern = args[2].toUpperCase();
        Life life = new Life(iter, dimension, pattern);
    }


}