/*
 * Copyright (C) 2021 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.tardischunkgenerator.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.ChatColor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TardisLogFilter implements Filter {

    private final String path;
    private final List<String> filters = new ArrayList<>();
    private boolean clean = true;

    public TardisLogFilter(String path) {
        this.path = path;
        filters.add("TARDIS");
        filters.add("tardis");
        filters.add("me.eccentric_nz");
        filters.add("Caused by:");
    }

    public Result checkMessage(String message) {
        for (String filter : filters) {
            if (message.contains(filter)) {
                writeToFile(ChatColor.stripColor(message));
                break;
            }
        }
        return Result.NEUTRAL;
    }

    private void writeToFile(String message) {
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        try {
            if (clean) {
                fileWriter = new FileWriter(path); // overwrite
                clean = false;
            } else {
                fileWriter = new FileWriter(path, true); // true to append
            }
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object... objects) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object object) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object object, Object object1) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object object, Object object1, Object object2) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object object, Object object1, Object object2, Object object3) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object object, Object object1, Object object2, Object object3, Object object4) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object object, Object object1, Object object2, Object object3, Object object4, Object object5) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object object, Object object1, Object object2, Object object3, Object object4, Object object5, Object object6) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object object, Object object1, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object object, Object object1, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String string, Object object, Object object1, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9) {
        return checkMessage(string);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object object, Throwable throwable) {
        return checkMessage(TextUtils.getStacktrace(throwable, true, "me.eccentric_nz."));
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message message, Throwable throwable) {
        return checkMessage(TextUtils.getStacktrace(throwable, true, "me.eccentric_nz."));
    }

    @Override
    public Result filter(LogEvent logEvent) {
        if (logEvent.getThrown() != null) {
            return checkMessage(TextUtils.getStacktrace(logEvent.getThrown(), true, "me.eccentric_nz."));
        }
        return checkMessage(logEvent.getMessage().getFormattedMessage());
    }

    @Override
    public State getState() {
        try {
            return State.STARTED;
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}