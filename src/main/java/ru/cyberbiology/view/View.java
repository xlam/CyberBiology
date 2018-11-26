package ru.cyberbiology.view;

import java.awt.Image;
import javax.swing.JPanel;
import ru.cyberbiology.BasicWorld;

public interface View {

    public Image paint(BasicWorld world, JPanel canvas);

    public String getName();
}
