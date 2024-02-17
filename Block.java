package assignment3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {

    private int xCoord;
    private int yCoord;
    private int size; // height/width of the square
    private int level; // the root (outer most block) is at level 0
    private int maxDepth;
    private Color color;
    private Block[] children; // {UR, UL, LL, LR}

    public static Random gen = new Random();


    /*
     * These two constructors are here for testing purposes.
     */
    public Block() {
    }

    public Block(int x, int y, int size, int lvl, int maxD, Color c, Block[] subBlocks) {
        this.xCoord = x;
        this.yCoord = y;
        this.size = size;
        this.level = lvl;
        this.maxDepth = maxD;
        this.color = c;
        this.children = subBlocks;
    }


    /*
     * Creates a random block given its level and a max depth.
     *
     * xCoord, yCoord, size, and highlighted should not be initialized
     * (i.e. they will all be initialized by default)
     */
    public Block(int lvl, int maxDepth) {
        if(lvl>maxDepth){
            throw new IllegalArgumentException("level is lower than maxDepth");
        }
        this.maxDepth = maxDepth;
        this.level = lvl;
        children= new Block[0];
        if (lvl != maxDepth) { //check max depth -1 if root counts
            double num = gen.nextDouble();
            if (num < Math.exp(-0.25 * level)) {
                Block UR = new Block(lvl + 1, maxDepth);
                Block UL = new Block(lvl + 1, maxDepth);
                Block LL = new Block(lvl + 1, maxDepth);
                Block LR = new Block(lvl + 1, maxDepth);
                children= new Block[]{UR, UL, LL, LR};
            }else{
                int block_color = gen.nextInt(GameColors.BLOCK_COLORS.length);
                this.color = GameColors.BLOCK_COLORS[block_color];
            }
        }
        else if(lvl== maxDepth){
            int block_color = gen.nextInt(GameColors.BLOCK_COLORS.length);
            this.color = GameColors.BLOCK_COLORS[block_color];
        }
    }


        /*
         * Updates size and position for the block and all of its sub-blocks, while
         * ensuring consistency between the attributes and the relationship of the
         * blocks.
         *
         *  The size is the height and width of the block. (xCoord, yCoord) are the
         *  coordinates of the top left corner of the block.
         */
        public void updateSizeAndPosition ( int size, int xCoord, int yCoord){

            this.xCoord= xCoord;
            this.yCoord= yCoord;
            if (size <= 0) {
                throw new IllegalArgumentException("size is not valid");
            }
            if(size%2 == 1 && level!= maxDepth){
                throw new IllegalArgumentException("size is not valid");
            }
            if(xCoord<0 || yCoord<0){
               throw new IllegalArgumentException("size is not valid");
            }
            if(children.length!= 0 ) {
                children[0].updateSizeAndPosition(size / 2, xCoord + size / 2, yCoord);
                children[1].updateSizeAndPosition(size / 2, xCoord, yCoord);
                children[2].updateSizeAndPosition(size / 2, xCoord, yCoord + size / 2);
                children[3].updateSizeAndPosition(size / 2, xCoord + size / 2, yCoord + size / 2);
            }
            this.size=size;

        }


        /*
         * Returns a List of blocks to be drawn to get a graphical representation of this block.
         *
         * This includes, for each undivided Block:
         * - one BlockToDraw in the color of the block
         * - another one in the FRAME_COLOR and stroke thickness 3
         *
         * Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
         *
         * The order in which the blocks to draw appear in the list does NOT matter.
         */
        public ArrayList<BlockToDraw> getBlocksToDraw () {
            ArrayList<BlockToDraw> blocks_array = new ArrayList<BlockToDraw>();
            if (children.length== 0) {
                BlockToDraw blockToDraw = new BlockToDraw(color, xCoord, yCoord, size, 0);
                blocks_array.add(blockToDraw);
                BlockToDraw frame = new BlockToDraw(GameColors.FRAME_COLOR, xCoord, yCoord, size, 3);
                blocks_array.add(frame);
            } else {
                for (Block subBlock : children) {
                    blocks_array.addAll(subBlock.getBlocksToDraw());
                }
            }
            return blocks_array;
        }






        /*
         * This method is provided and you should NOT modify it.
         */
        public BlockToDraw getHighlightedFrame () {
            return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
        }



        /*
         * Return the Block within this Block that includes the given location
         * and is at the given level. If the level specified is lower than
         * the lowest block at the specified location, then return the block
         * at the location with the closest level value.
         *
         * The location is specified by its (x, y) coordinates. The lvl indicates
         * the level of the desired Block. Note that if a Block includes the location
         * (x, y), and that Block is subdivided, then one of its sub-Blocks will
         * contain the location (x, y) too. This is why we need lvl to identify
         * which Block should be returned.
         *
         * Input validation:
         * - this.level <= lvl <= maxDepth (if not throw exception)
         * - if (x,y) is not within this Block, return null.
         */
        public Block getSelectedBlock ( int x, int y, int lvl){
            if(lvl< this.level || lvl>maxDepth){
                throw new IllegalArgumentException();
            }
            if(children.length==0 || lvl== level){
                return this;
            }
            if(!containsCoordinates(x,y)){
                return null;
            }
            for (Block subBlock : children){
                if(subBlock.containsCoordinates(x,y)){
                    return subBlock.getSelectedBlock(x, y, lvl);
                }
            }
            return null;
        }
        // Helper method to check if the Block selected contains the coordinates of the pointer
        private boolean containsCoordinates(int x, int y){
            return x >= this.xCoord && y >= this.yCoord && x <= this.xCoord + this.size && y <= this.yCoord + this.size;
        }

        /*
         * Swaps the child Blocks of this Block.
         * If input is 1, swap vertically. If 0, swap horizontally.
         * If this Block has no children, do nothing. The swap
         * should be propagate, effectively implementing a reflection
         * over the x-axis or over the y-axis.
         *
         */
        public void reflect ( int direction){
            if (direction != 0 && direction != 1) {
                throw new IllegalArgumentException("Number is not valid");
            }
            if (children.length!= 0) {
                if(direction==0){
                    Block reflect1 = children[0];
                    Block reflect2= children[1];
                    children[0]= children[3];
                    children[1]= children[2];
                    children[2]= reflect2;
                    children[3]= reflect1;
                }
                if(direction==1){
                    Block reflect1= children[0];
                    Block reflect2= children[3];
                    children[0]= children[1];
                    children[3]= children[2];
                    children[1]= reflect1;
                    children[2]= reflect2;
                }
                for (Block subBlock : children) {
                    subBlock.reflect(direction);
                }
            }
            this.updateSizeAndPosition(size, xCoord, yCoord);
            }




        /*
         * Rotate this Block and all its descendants.
         * If the input is 1, rotate clockwise. If 0, rotate
         * counterclockwise. If this Block has no children, do nothing.
         */
        public void rotate ( int direction) {
            if (direction != 0 && direction != 1) {
                throw new IllegalArgumentException("Number is not valid");
            }
            if (children.length != 0) {
                if (direction == 0) {
                    Block counter1 = children[0];
                    Block counter2 = children[1];
                    children[0] = children[3];
                    children[1] = counter1;
                    children[3] = children[2];
                    children[2] = counter2;
                }
                if (direction == 1) {
                    Block clockwise1 = children[0];
                    Block clockwise2 = children[3];
                    children[0] = children[1];
                    children[3] = clockwise1;
                    children[1] = children[2];
                    children[2] = clockwise2;
                for (Block subBlock : children) {
                        subBlock.rotate(direction);
                    }
                }
            }
            this.updateSizeAndPosition(size, xCoord, yCoord);
        }



        /*
         * Smash this Block.
         *
         * If this Block can be smashed,
         * randomly generate four new children Blocks for it.
         * (If it already had children Blocks, discard them.)
         * Ensure that the invariants of the Blocks remain satisfied.
         *
         * A Block can be smashed iff it is not the top-level Block
         * and it is not already at the level of the maximum depth.
         *
         * Return True if this Block was smashed and False otherwise.
         *
         */
        public boolean smash () {
            if(level>= maxDepth || level==0){
                return false;
            }
            if(children.length!=0){
                for(int i=0; i<children.length; i++){
                    children[i]= null;
                }
            }
            children= new Block[4];
            for(int j=0; j<4; j++){
                children[j]= new Block(level+1,maxDepth);
            }
            this.updateSizeAndPosition(size, xCoord, yCoord);
            return true;

        }


        /*
         * Return a two-dimensional array representing this Block as rows and columns of unit cells.
         *
         * Return and array arr where, arr[i] represents the unit cells in row i,
         * arr[i][j] is the color of unit cell in row i and column j.
         *
         * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
         */
        public Color[][] flatten () {
            int total_size = (int) Math.pow(2, maxDepth-level);
            Color[][] arr= new Color[total_size][total_size];
            if(children.length==0)
                for(int i= 0; i<total_size; i++){
                    for(int j= 0; j<total_size; j++){
                        arr[i][j]= this.color;
                }
            }
            else{
                Color[][] TL = children[0].flatten();
                Color[][] TR= children[1].flatten();
                Color[][] BL= children[2].flatten();
                Color[][] BR= children[3].flatten();
                for(int i=0; i<total_size/2;i++){
                    for(int k=0; k<total_size/2; k++){
                        arr[i][k]= TR[i][k];
                        arr[i][k+total_size/2]= TL[i][k];
                        arr[i+total_size/2][k]= BL[i][k];
                        arr[i+total_size/2][k+total_size/2]= BR[i][k];
                    }
                }
            }
            return arr;
        }


        // These two get methods have been provided. Do NOT modify them.
        public int getMaxDepth () {
            return this.maxDepth;
        }

        public int getLevel () {
            return this.level;
        }


        /*
         * The next 5 methods are needed to get a text representation of a block.
         * You can use them for debugging. You can modify these methods if you wish.
         */
        public String toString () {
            return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
        }

        public void printBlock () {
            this.printBlockIndented(0);
        }

        private void printBlockIndented ( int indentation){
            String indent = "";
            for (int i = 0; i < indentation; i++) {
                indent += "\t";
            }

            if (this.children.length == 0) {
                // it's a leaf. Print the color!
                String colorInfo = GameColors.colorToString(this.color) + ", ";
                System.out.println(indent + colorInfo + this);
            } else {
                System.out.println(indent + this);
                for (Block b : this.children)
                    b.printBlockIndented(indentation + 1);
            }
        }

        private static void coloredPrint (String message, Color color){
            System.out.print(GameColors.colorToANSIColor(color));
            System.out.print(message);
            System.out.print(GameColors.colorToANSIColor(Color.WHITE));
        }

        public void printColoredBlock () {
            Color[][] colorArray = this.flatten();
            for (Color[] colors : colorArray) {
                for (Color value : colors) {
                    String colorName = GameColors.colorToString(value).toUpperCase();
                    if (colorName.length() == 0) {
                        colorName = "\u2588";
                    } else {
                        colorName = colorName.substring(0, 1);
                    }
                    coloredPrint(colorName, value);
                }
                System.out.println();
            }
        }
    }