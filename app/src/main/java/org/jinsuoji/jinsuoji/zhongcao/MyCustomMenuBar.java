package org.jinsuoji.jinsuoji.zhongcao;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.goyourfly.multiple.adapter.R.id;
import com.goyourfly.multiple.adapter.R.menu;
import com.goyourfly.multiple.adapter.menu.CustomMenuBar;
import com.goyourfly.multiple.adapter.menu.MenuController;

public final class MyCustomMenuBar extends CustomMenuBar {
    private final int color;

    public void onMenuItemClick(@NonNull MenuItem menuItem, @NonNull MenuController controller) {
        int var3 = menuItem.getItemId();
        if (var3 == id.action_done) {
            this.dismiss();
            controller.done(false);
        } else if (var3 == id.action_delete) {
            this.dismiss();
            controller.delete(false);
        } else if (var3 == id.action_all) {
            controller.selectAll();
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

    MyCustomMenuBar(Activity activity, int color, int gravity) {
        super(activity, menu.menu_multiple_select_done_delete_all, color, gravity);
        this.color = color;
    }
}
