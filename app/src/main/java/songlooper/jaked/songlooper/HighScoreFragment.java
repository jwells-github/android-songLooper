package songlooper.jaked.songlooper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class HighScoreFragment extends Fragment {

    // The song titles of the high scores
    private TextView songOne;
    private TextView songTwo;
    private TextView songThree;
    private TextView songFour;
    private TextView songFive;
    // The scores of all of hte high scores
    private TextView playsOne;
    private TextView playsTwo;
    private TextView playsThree;
    private TextView playsFour;
    private TextView playsFive;

    private int[] plays = {0,0,0,0,0};
    private String[] songTitles = {"No record","No record","No record","No record","No record"};



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_highscores, container, false);
        songOne = v.findViewById(R.id.tvHighScoreOne);
        songTwo = v.findViewById(R.id.tvHighScoreTwo);
        songThree = v.findViewById(R.id.tvHighScoreThree);
        songFour = v.findViewById(R.id.tvHighScoreFour);
        songFive = v.findViewById(R.id.tvHighScoreFive);


        songOne.setText(songTitles[0]);
        songTwo.setText(songTitles[1]);
        songThree.setText(songTitles[2]);
        songFour.setText(songTitles[3]);
        songFive.setText(songTitles[4]);

        playsOne = v.findViewById(R.id.tvPlaysOne);
        playsTwo = v.findViewById(R.id.tvPlaysTwo);
        playsThree = v.findViewById(R.id.tvPlaysThree);
        playsFour = v.findViewById(R.id.tvPlaysFour);
        playsFive = v.findViewById(R.id.tvPlaysFive);

        playsOne.setText(String.valueOf(plays[0]));
        playsTwo.setText(String.valueOf(plays[1]));
        playsThree.setText(String.valueOf(plays[2]));
        playsFour.setText(String.valueOf(plays[3]));
        playsFive.setText(String.valueOf(plays[4]));

        // Get the highscores from file
        getScores();
        return v;
    }

    // Get the highscores from file
    // Get the saved user scores
    public void getScores(){
        try{
            FileInputStream fileInputStream = getContext().openFileInput("userSettings.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            char[] inputBuffer = new char[500];
            StringBuilder s = new StringBuilder();
            int charRead;
            while ((charRead = inputStreamReader.read(inputBuffer)) > 0){
                String readString = String.copyValueOf(inputBuffer,0,charRead);
                s.append(readString);
            }
            inputStreamReader.close();
            // Split the string on new line
            String[] splitString = s.toString().split("\n");
            int counter = 0;
            // Alternate between adding each string to the SongTitles array and the plays array
            for (int i = 0; i < splitString.length; i++){
                if((i+2) % 2 == 0){
                    songTitles[counter] = splitString[i];
                }
                else{
                    plays[counter] = Integer.valueOf(splitString[i]);
                    counter++;
                }
            }

            // Set the high scores
            songOne.setText(songTitles[0]);
            songTwo.setText(songTitles[1]);
            songThree.setText(songTitles[2]);
            songFour.setText(songTitles[3]);
            songFive.setText(songTitles[4]);

            playsOne.setText(String.valueOf(plays[0]));
            playsTwo.setText(String.valueOf(plays[1]));
            playsThree.setText(String.valueOf(plays[2]));
            playsFour.setText(String.valueOf(plays[3]));
            playsFive.setText(String.valueOf(plays[4]));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
