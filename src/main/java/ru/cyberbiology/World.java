package ru.cyberbiology;

import ru.cyberbiology.gene.Gene;

public interface World {

    public int getWidth();

    public int getHeight();

    public void setSize(int width, int height);

    public void setBot(BasicBot bot);

    public void clearBot(int x, int y);

    public void paint();

    public BasicBot[] getWorldArray();

    public void restoreLinks();

    public void restoreStats();

    public Gene[] getGenes();
}
