package ru.cyberbiology;

import ru.cyberbiology.util.ProjectProperties;
import ru.cyberbiology.view.View;

public interface Window {

    public void paint();

    public void setView(View view);

    public ProjectProperties getProperties();

}
