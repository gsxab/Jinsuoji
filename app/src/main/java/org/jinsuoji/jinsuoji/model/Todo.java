package org.jinsuoji.jinsuoji.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Todo implements Serializable {
    public Todo(@NonNull Date dateTime, @NonNull String taskName, int priority, @NonNull String memo, boolean finished) {
        this.dateTime = dateTime;
        this.taskName = taskName;
        this.priority = priority;
        this.memo = memo;
        this.finished = finished;
    }

    private @NonNull Date dateTime;

    private @NonNull String taskName;
    private int priority;
    private @NonNull String memo;
    private boolean finished;

    public @NonNull Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(@NonNull Date dateTime) {
        this.dateTime = dateTime;
    }

    public @NonNull String getTaskName() {
        return taskName;
    }

    public void setTaskName(@NonNull String taskName) {
        this.taskName = taskName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public @NonNull String getMemo() {
        return memo;
    }

    public void setMemo(@NonNull String memo) {
        this.memo = memo;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
