package songlooper.jaked.songlooper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class SongLooperFragment extends Fragment {

    private Button mBtnStart;
    private Button mBtnStop;
    private ImageView mIvCurrent;
    private SeekBar mSbSongProgress;
    private TextView mTvSongPlays;

    public static SongLooperFragment newInstance() {
        return new SongLooperFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_songlooper, container, false);

        mBtnStart = (Button) v.findViewById(R.id.btnStart);
        mBtnStop = (Button) v.findViewById(R.id.btnStop);

        mIvCurrent = (ImageView) v.findViewById(R.id.ivCurrent);

        mSbSongProgress = (SeekBar) v.findViewById(R.id.sbSongProgress);

        mTvSongPlays = (TextView) v.findViewById(R.id.tvSongPlays);
        return v;
    }
}
