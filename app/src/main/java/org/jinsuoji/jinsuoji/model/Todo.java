package org.jinsuoji.jinsuoji.model;

import android.content.Context;

import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务类.
 * 保存一次任务条目的基本信息.
 */
public class Todo implements Serializable, ContextualStringConvertible {
    public Todo(int id, Date dateTime, String taskName, String memo, Date reminderTime, boolean finished) {
        this.id = id;
        this.dateTime = dateTime;
        this.taskName = taskName;
        this.memo = memo;
        this.reminderTime = reminderTime;
        this.finished = finished;
    }

    private int id;
    private Date dateTime;
    private String taskName;
    private Date reminderTime;
    private String memo;
    private boolean finished;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Date getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Date reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toContextualString(Context context) {
        return context.getString(R.string.todo_format, taskName, DateUtils.toDateTimeString(dateTime));
    }
}
