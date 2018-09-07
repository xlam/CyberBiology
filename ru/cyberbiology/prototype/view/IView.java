package ru.cyberbiology.prototype.view;

import java.awt.Image;

import javax.swing.JPanel;

import ru.cyberbiology.World;

public interface IView
{
	public Image paint(World world,JPanel canvas);

	public String getName();
}
