package ru.cyberbiology.prototype;

import ru.cyberbiology.prototype.view.IView;
import ru.cyberbiology.util.ProjectProperties;

public interface IWindow
{

	public void paint();

	public void setView(IView view);

	public ProjectProperties getProperties();

}
