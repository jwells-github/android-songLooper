package songlooper.jaked.songlooper;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class SongLooperFragment extends Fragment {

    private ImageView mIvCurrent;
    private SeekBar mSbSongProgress;
    private TextView mTvSongPlays;
    private TextView mTvSongTitle;
    private Song mSong;
    private MediaPlayer mMediaPlayer;
    // The progress of the currently playing song
    private int mSongProgress = 0;
    // The number of plays for the current song
    private int mPlays = 0;
    private Uri mSongUri;
    // Whether or not a song is playing.
    private boolean mIsPlaying = false;

    private thread mThread;

    static final int AUDIO_REQUEST = 1;

    private static final String KEY_PLAYS = "plays";
    private static final String KEY_SONGPROGRESS = "songProgress";
    private static final String KEY_SONGURI = "songUri";
    private static final String KEY_PLAYING = "playing";

    // An array to trecord the high scores and their corresponding song titles.
    private int[] plays = {0,0,0,0,0};
    private String[] songTitles = {"No record","No record","No record","No record","No record"};


    public static SongLooperFragment newInstance() {
        return new SongLooperFragment();
    }

    // Save the current user scores to file
    public void saveScores() {
        Boolean usedBefore = false;
        int usedSongPos = 0;
        String[] changedTitles = new String[5];
        int[] changedPlays = new int[5];

        // Checks if the song title already has a high score recored for it
        for (int i = 0; i < songTitles.length; i++) {
            if (songTitles[i].equals(mSong.getSongTitle())) {
                usedBefore = true;
                // The array position of the previously saved score
                usedSongPos = i;
            }
        }

        // If the song title already has a high score recorded for it
        if (usedBefore) {
            // If the song is at the top of the high score table
            if(usedSongPos == 0){
                // If the new score is higher than the recorded score, replace it
                if(mPlays > plays[usedSongPos]){
                    plays[usedSongPos] = mPlays;
                }
            }
            // If the score is a new record, but not higher than the record above it, replace it
            else if(plays[usedSongPos] < plays[usedSongPos -1]){
                if (mPlays > plays[usedSongPos]){
                    plays[usedSongPos] = mPlays;
                }
            }
            // Check every record before the new high score
            for (int i = 0; i < usedSongPos; i++) {
                // If the record is less than the new high score
                if (plays[i] < mPlays) {
                    // Add every record before i to an array
                    for (int j = 0; j < i; j++) {
                        changedTitles[j] = songTitles[j];
                        changedPlays[j] = plays[j];
                    }
                    // Add the new high score in the place of I
                    changedTitles[i] = songTitles[usedSongPos];
                    changedPlays[i] = mPlays;
                    // Add the replaced high score after new high score
                    if(i < songTitles.length){
                        changedTitles[i + 1] = songTitles[i];
                        changedPlays[i + 1] = plays[i];
                    }
                    // Add all remaining records to the new array
                    for (int j = i + 2; j < songTitles.length; j++) {
                        System.out.println("added after our inserted record " + songTitles[j] + " " + plays[j]);
                        changedTitles[j] = songTitles[j];
                        changedPlays[j] = plays[j];
                    }
                    // apply the new high scores to the arrays.
                    plays = changedPlays;
                    songTitles = changedTitles;
                    break;
                }
            }
        } else {
            for (int i = 0; i < plays.length; i++) {
                // Check if the current score is larger than any of the saved high scores
                if (plays[i] < mPlays) {
                    // Add all scores higher than the new score to the array
                    for (int j = 0; j < i; j++) {
                        changedTitles[j] = songTitles[j];
                        changedPlays[j] = plays[j];
                    }
                    // Add the new score to the array
                    changedTitles[i] = mSong.getSongTitle();
                    changedPlays[i] = mPlays;
                    // Add any remaining scores to the array, up to five.
                    for (int j = i+1; j < songTitles.length; j++) {
                        changedTitles[j] = songTitles[j];
                        changedPlays[j] = plays[j];
                    }
                    // apply the new high scores to the arrays.
                    plays = changedPlays;
                    songTitles = changedTitles;
                    break;
                }
            }
        }
        // Write the new scores to file, seperating each song title and score with a new line.
        try {
            FileOutputStream fileOutputStream = getContext().openFileOutput("userSettings.txt", MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(
                    songTitles[0] + "\n" +
                            plays[0] + "\n" +
                            songTitles[1] + "\n"  +
                            plays[1] + "\n" +
                            songTitles[2] + "\n"  +
                            plays[2] + "\n" +
                            songTitles[3] + "\n"  +
                            plays[3] + "\n" +
                            songTitles[4] + "\n"  +
                            plays[4]);
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    // Saves neccessary variables when the screen rotates
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Number of plays of the current song
        outState.putInt(KEY_PLAYS, mPlays);
        // The current progress of the playing song
        outState.putInt(KEY_SONGPROGRESS, mSongProgress);
        // Whether or not a song is playing
        outState.putBoolean(KEY_PLAYING, mIsPlaying);
        // the URI pointing to the currently selected song
        if(mSongUri != null){
            outState.putString(KEY_SONGURI,mSongUri.toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTvSongPlays.setText(String.valueOf(mPlays));
    }

    private void StartMediaPlayer(){
        if(mMediaPlayer != null){
            mMediaPlayer.start();
            if (mSongProgress != 0 ){
                mMediaPlayer.seekTo(mSongProgress);
            }
            // Starts the song again when it is finished
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mTvSongPlays.setText(String.valueOf(mPlays));
                    mSongProgress = 0;
                    saveScores();
                    getScores();
                    StartMediaPlayer();
                }
            });
            mSbSongProgress.setMax(mMediaPlayer.getDuration());
            // Launches a thread to keep track of the current song progress
            mThread = new thread();
            mThread.start();


        }
    }

   class thread extends Thread{
        @Override
        public void run() {
            try{
                // Updates the Seekbar to the current song progress.
                while(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                    mSongProgress = mMediaPlayer.getCurrentPosition();
                    mSbSongProgress.setProgress(mSongProgress);
                }
                // Increment the number of plays when the song is finished
                if(mSbSongProgress.getProgress() == mSbSongProgress.getMax()){
                    mPlays++;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Creates a file for the userSettings if it doesn't already exist.
        File file = new File("userSettings.txt");
        if(!file.exists()){
            System.out.println("we have made a new file ");
            try{
                FileOutputStream fileOutputStream = getContext().openFileOutput("userSettings.txt", MODE_PRIVATE);
                OutputStreamWriter outputStreamWriter  = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.write("No Record\n0\nNo Record\n0\nNo Record\n0\nNo Record\n0\nNo Record\n0");
                outputStreamWriter.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_songlooper,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            // Launch the high scores fragment
            case R.id.menu_high_scores:
                HighScoreFragment highScoreFragment = new HighScoreFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,highScoreFragment,"highScoresFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_songlooper, container, false);
        Button btnStart = v.findViewById(R.id.btnStart);
        btnStart.setText(R.string.start);

        // If a song is selected, start the song.
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMediaPlayer != null && !mMediaPlayer.isPlaying() ){
                    mIsPlaying = true;
                    StartMediaPlayer();
                }
            }
        });


        Button btnStop = v.findViewById(R.id.btnStop);
        btnStop.setText(R.string.stop);

        // If a song is selected, pause it.
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                    mIsPlaying = false;
                    mMediaPlayer.pause();
                }
            }
        });


        mIvCurrent = v.findViewById(R.id.ivCurrent);
        mIvCurrent.setImageResource(R.drawable.choosesong);
        // When clicked, prompt the user to select an audio file.
        mIvCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");

                Intent finalIntent = Intent.createChooser(intent, "select song");
                startActivityForResult(finalIntent, AUDIO_REQUEST);
            }
        });

        mTvSongPlays =  v.findViewById(R.id.tvSongPlays);
        mTvSongPlays.setText(String.valueOf(mPlays));


        mSbSongProgress = v.findViewById(R.id.sbSongProgress);
        mSbSongProgress.setSoundEffectsEnabled(false);
        if(mMediaPlayer != null){
            mSbSongProgress.setMax(mMediaPlayer.getDuration());
            mSbSongProgress.setProgress(mSongProgress);
        }


        // Prevents the user from interacting with the seekbar.
        mSbSongProgress.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


        mTvSongTitle = v.findViewById(R.id.tvSongTitle);


        if(savedInstanceState != null){
            mMediaPlayer = new MediaPlayer();
            // Get the song that was previously selected.
            mSongUri = Uri.parse(savedInstanceState.getString(KEY_SONGURI,""));
            // If a song is found.
            if(!savedInstanceState.getString(KEY_SONGURI,"").equals("")){

                mIsPlaying = savedInstanceState.getBoolean(KEY_PLAYING, false);
                mSong = new Song(mSongUri, getContext());
                mPlays = savedInstanceState.getInt(KEY_PLAYS,0);
                mSongProgress = savedInstanceState.getInt(KEY_SONGPROGRESS, 0);
                try {
                    mMediaPlayer.setDataSource(getContext(),mSongUri);
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // If a song was saved on rotation, set the song title and image art.
            if(mSong!=null){
                mTvSongTitle.setText(mSong.getSongTitle());
                if(mSong.getFileArt()!= null ){
                    mIvCurrent.setImageBitmap(mSong.getFileArt());
                }
                else{
                    mIvCurrent.setImageResource(R.drawable.nocover);
                }
            }
            // If the song was playing, strat it again.
            if(mIsPlaying){
                StartMediaPlayer();
            }
        }

        // Get the user's high scores.
        getScores();
        return v;
    }

    @Override
    public void onDestroy() {
        // Save the user's score.
        if(mSong!= null){
            saveScores();
        }
        // Stop the Media player and release it.
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            // If the thread is still running, get it to finish before closing.
            if(mThread!= null && mThread.isAlive()){
                try {
                    mThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onDestroy();
    }

    // Once the user has chosen a song to play.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUDIO_REQUEST){
            if (resultCode == RESULT_OK){
                // get the URI location of the song file
                mSongUri = data.getData();
                // Create a new song object
                mSong = new Song(mSongUri, getContext());
                try {
                    // If a song is already playing, stop it.
                    if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        mMediaPlayer.stop();
                    }
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDataSource(getContext(),mSongUri);
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Set the image view to the song's file art if there is any.
                if(mSong.getFileArt()!= null){
                    mIvCurrent.setImageBitmap(mSong.getFileArt());
                }
                else{
                    mIvCurrent.setImageResource(R.drawable.nocover);
                }
                mTvSongTitle.setText(mSong.getSongTitle());
                mPlays = 0;
                mTvSongPlays.setText("0");
                getScores();
            }
        }
    }
}
