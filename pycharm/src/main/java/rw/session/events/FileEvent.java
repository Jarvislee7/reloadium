package rw.session.events;

import java.io.File;

abstract public class FileEvent extends Event {
    public static final String ID = "FileEvent";
    protected String path;

    public File getLocalPath() {
        return new File(this.handler.convertPathToLocal(this.path, true));
    }

    public File getPath() {
        return new File(path);
    }
}
