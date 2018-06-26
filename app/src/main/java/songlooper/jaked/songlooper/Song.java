package songlooper.jaked.songlooper;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;


public class Song {

    // The songs cover image if it has one
    private Bitmap mFileArt;
    // The title of the song
    private String mSongTitle;

    public Song(Uri uri, Context context){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,uri);
        // Retrieves the song titile
        mSongTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) + " by " + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);;


        byte[] rawArt;
        BitmapFactory.Options bfo = new BitmapFactory.Options();

        mmr.setDataSource(context,uri);
        rawArt = mmr.getEmbeddedPicture();

        if (null != rawArt){
            mFileArt = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        }
    }
    public Bitmap getFileArt() {
        return mFileArt;
    }
    public String getSongTitle() {
        return mSongTitle;
    }
}
