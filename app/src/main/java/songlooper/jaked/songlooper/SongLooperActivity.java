package songlooper.jaked.songlooper;

        import android.content.Context;
        import android.content.Intent;
        import android.support.v4.app.Fragment;

public class SongLooperActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SongLooperFragment.newInstance();
    }
}
