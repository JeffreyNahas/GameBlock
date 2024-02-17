package assignment3;

import java.awt.Color;

public class BlobGoal extends Goal{

    public BlobGoal(Color c) {
        super(c);
    }

    @Override
    public int score(Block board) {
        Color[][] arr = board.flatten();
        int maxBlobSize = 0;
        boolean[][] visited = new boolean[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                int blobSize = undiscoveredBlobSize(i, j, arr, visited);
                if (blobSize > maxBlobSize) {
                    maxBlobSize = blobSize;
                }
            }
        }
        return maxBlobSize;
    }


    @Override
    public String description() {
        return "Create the largest connected blob of " + GameColors.colorToString(targetGoal)
                + " blocks, anywhere within the block";
    }


    public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
        if (i < 0 || i >= unitCells.length || j < 0 || j >= unitCells[0].length ||
                !unitCells[i][j].equals(targetGoal) || visited[i][j]) {
            return 0;
        }
        visited[i][j] = true;
        int blobSize = 1; //size of current cell
        blobSize += undiscoveredBlobSize(i - 1, j, unitCells, visited); // up
        blobSize += undiscoveredBlobSize(i + 1, j, unitCells, visited); // down
        blobSize += undiscoveredBlobSize(i, j - 1, unitCells, visited); // left
        blobSize += undiscoveredBlobSize(i, j + 1, unitCells, visited); // right
        return blobSize;
    }

}
