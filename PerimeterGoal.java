package assignment3;

import java.awt.Color;

public class PerimeterGoal extends Goal{

    public PerimeterGoal(Color c) {
        super(c);
    }

    @Override
    public int score(Block board) {
        Color[][] arr= board.flatten();
        int score= 0;
        for(int i=0; i<arr.length-1; i++) {
            if (arr[0][i] == targetGoal) {
                score++;
            }
            if(arr[i][0]== targetGoal){
                score++;
            }
            if(arr.length>2) {
                if (arr[arr.length - 1][i] == targetGoal) {
                    score++;
                }
                if (arr[i][arr[0].length - 1] == targetGoal) {
                    score++;
                }
            }
        }
        if(arr[arr.length-1][arr[0].length-1]== targetGoal){
            score ++;
        }
        if(arr[0][0]== targetGoal){
            score++;
        }
        if(arr[0][arr[0].length-1]==targetGoal){
            score++;
        }
        if(arr[arr.length-1][0]== targetGoal){
            score++;
        }
        return score;

    }

    @Override
    public String description() {
        return "Place the highest number of " + GameColors.colorToString(targetGoal)
                + " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
    }

}

