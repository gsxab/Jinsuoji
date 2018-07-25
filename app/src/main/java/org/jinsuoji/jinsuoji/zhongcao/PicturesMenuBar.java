package org.jinsuoji.jinsuoji.zhongcao;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.goyourfly.multiple.adapter.menu.CustomMenuBar;
import com.goyourfly.multiple.adapter.menu.MenuController;

import org.jinsuoji.jinsuoji.R;

import java.util.List;

public class PicturesMenuBar extends CustomMenuBar {
    private final int color;
    private MenuBarCallback callback;

    public void onMenuItemClick(@NonNull MenuItem menuItem, @NonNull MenuController controller) {
        int menuItemId = menuItem.getItemId();
        if (menuItemId == R.id.action_all) {
            controller.selectAll();
        } else {
            List<Integer> selected = controller.getSelect();
            if (callback.action(menuItemId, selected)) {
                cancel();
            }
        }
    }

    public boolean cancel() {
        this.dismiss();
        MenuController controller = this.getControler();
        return controller != null && controller.cancel(true);
    }

    public final int getColor() {
        return this.color;
    }

    PicturesMenuBar(Activity activity, int color, int gravity, MenuBarCallback callback) {
        super(activity, R.menu.select_category, color, gravity);
        this.color = color;
        this.callback = callback;
    }
}
