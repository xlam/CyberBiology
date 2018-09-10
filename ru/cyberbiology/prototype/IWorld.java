package ru.cyberbiology.prototype;

import ru.cyberbiology.Bot;
import ru.cyberbiology.util.ProjectProperties;

public interface IWorld {

    public int getWidth();

    public int getHeight();

    public void setSize(int width, int height);

    public void setBot(Bot bot);

    public void clearBot(int x, int y);

    public void paint();

    public ProjectProperties getProperties();

    public Bot[] getWorldArray();

    public void restoreLinks();

    public void restoreStats();

}
