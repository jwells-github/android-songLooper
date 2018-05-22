package songlooper.jaked.songlooper;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SongLooperActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context){
        return new Intent(context, SongLooperActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return SongLooperFragment.newInstance();
    }
}
