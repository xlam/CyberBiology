package ru.cyberbiology;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import ru.cyberbiology.prototype.IWindow;
import ru.cyberbiology.prototype.gene.IBotGeneController;
import ru.cyberbiology.prototype.view.IView;
import ru.cyberbiology.util.PerfMeter;
import ru.cyberbiology.util.ProjectProperties;
import ru.cyberbiology.view.ViewBasic;
import ru.cyberbiology.view.ViewEnergy;
import ru.cyberbiology.view.ViewMultiCell;
import ru.cyberbiology.view.ViewPest;

public class MainWindow extends JFrame implements IWindow {

    JMenuItem runItem;
    JMenuItem mutateItem;

    public static MainWindow window;

    public static final int BOTW = 4;
    public static final int BOTH = 4;

    public static World world;
    // JPanel paintPanel = new JPanel(new FlowLayout());

    public JLabel generationLabel = new JLabel(" Generation: 0 ");
    public JLabel populationLabel = new JLabel(" Population: 0 ");
    public JLabel organicLabel = new JLabel(" Organic: 0 ");
    public JLabel perfLabel = new JLabel(" WIPS: 0 ");
    public JLabel pestsLabel = new JLabel(" Pests: 0 ");
    public JLabel pestGenesLabel = new JLabel(" Pest genes: 0 ");

    public JLabel recorderBufferLabel = new JLabel("");
    public JLabel memoryLabel = new JLabel("");

    public JLabel frameSavedCounterLabel = new JLabel("");
    public JLabel frameSkipSizeLabel = new JLabel("");

    /**
     * буфер для отрисовки ботов
     */
    public Image buffer = null;

    /**
     * актуальный отрисовщик
     */
    IView view;

    /**
     * Перечень возможных отрисовщиков
     */
    IView[] views = new IView[]{
        new ViewBasic(),
        new ViewEnergy(),
        new ViewMultiCell(),
        new ViewPest()
    };
    JMenuItem recordItem;
    JMenuItem snapShotItem;
    JMenuItem adressJumpItem;

    public JPanel paintPanel = new JPanel() {
        public void paint(Graphics g) {
            g.drawImage(buffer, 0, 0, null);
        }
    };

    ProjectProperties properties;

    public MainWindow() {
        window = this;
        properties = new ProjectProperties("properties.xml");

        setTitle("CyberBiologyTest 1.0.0");
        setPreferredSize(new Dimension(1024, 768));
//        setSize(new Dimension(1024, 768));
//        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize(), fSize = getSize();
//        if (fSize.height > sSize.height) { fSize.height = sSize.height; }
//        if (fSize.width  > sSize.width)  { fSize.width = sSize.width; }
//        //setLocation((sSize.width - fSize.width)/2, (sSize.height - fSize.height)/2);
//        setSize(new Dimension(sSize.width, sSize.height));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Container container = getContentPane();

        container.setLayout(new BorderLayout());// у этого лейаута приятная особенность - центральная часть растягивается автоматически
        container.add(paintPanel, BorderLayout.CENTER);// добавляем нашу карту в центр

        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        container.add(statusPanel, BorderLayout.SOUTH);

        generationLabel.setPreferredSize(new Dimension(140, 18));
        generationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(generationLabel);

        populationLabel.setPreferredSize(new Dimension(140, 18));
        populationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(populationLabel);

        organicLabel.setPreferredSize(new Dimension(140, 18));
        organicLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(organicLabel);

        pestsLabel.setPreferredSize(new Dimension(140, 18));
        pestsLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(pestsLabel);

        pestGenesLabel.setPreferredSize(new Dimension(140, 18));
        pestGenesLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(pestGenesLabel);

        perfLabel.setPreferredSize(new Dimension(140, 18));
        perfLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(perfLabel);

        memoryLabel.setPreferredSize(new Dimension(140, 18));
        memoryLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(memoryLabel);

        recorderBufferLabel.setPreferredSize(new Dimension(140, 18));
        recorderBufferLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(recorderBufferLabel);

        frameSavedCounterLabel.setPreferredSize(new Dimension(140, 18));
        frameSavedCounterLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(frameSavedCounterLabel);

        frameSkipSizeLabel.setPreferredSize(new Dimension(140, 18));
        frameSkipSizeLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(frameSkipSizeLabel);

        paintPanel.addMouseListener(new CustomListener());

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        runItem = new JMenuItem("Запустить");
        fileMenu.add(runItem);
        runItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (world == null) {
                    int width = paintPanel.getWidth() / BOTW;// Ширина доступной части экрана для рисования карты
                    int height = paintPanel.getHeight() / BOTH;// Боты 4 пикселя?
                    world = new World(window, width, height);
                    world.generateAdam();
                    paint();
                }
                if (!world.started()) {
                    world.start();//Запускаем его
                    runItem.setText("Пауза");

                } else {
                    world.stop();
                    runItem.setText("Продолжить");
                    snapShotItem.setEnabled(true);
                }

            }
        });

        mutateItem = new JMenuItem("Cлучайная мутация");
        fileMenu.add(mutateItem);
        mutateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // мутацию проводим при отключенном мире
                world.stop();
                world.randomMutation(10, 32);
                world.start();
            }
        });

        adressJumpItem = new JMenuItem("Сбой программы генома");
        fileMenu.add(adressJumpItem);
        adressJumpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO переименовать
                world.jumpBotsCmdAdress();
            }
        });

        snapShotItem = new JMenuItem("Сделать снимок");
        fileMenu.add(snapShotItem);
//        snapShotItem.setEnabled(false);
        snapShotItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (world == null) {
                    int width = paintPanel.getWidth() / BOTW;// Ширина доступной части экрана для рисования карты
                    int height = paintPanel.getHeight() / BOTH;// Боты 4 пикселя?
                    world = new World(window, width, height);
                    world.generateAdam();
                    paint();
                }
                world.stop();
                runItem.setText("Продолжить");
                world.makeSnapShot();
            }
        });

        recordItem = new JMenuItem("Начать запись");
        fileMenu.add(recordItem);

        recordItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (world == null) {
                    int width = paintPanel.getWidth() / BOTW;// Ширина доступной части экрана для рисования карты
                    int height = paintPanel.getHeight() / BOTH;// Боты 4 пикселя?
                    world = new World(window, width, height);
                    world.generateAdam();
                    paint();
                }
                if (!world.isRecording()) {
                    world.startRecording();
                    recordItem.setText("Сохранить запись");
                } else {
                    recordItem.setText("Начать запись");

                    world.stopRecording();
                    if (world.haveRecord()) {
                        //saveItem.setEnabled(true);
                        //deleteItem.setEnabled(true);
                        //recordItem.setEnabled(false);
                    }
                }
            }
        });

        JMenuItem openItem = new JMenuItem("Открыть плеер");
        fileMenu.add(openItem);
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PlayerWindow fw = new PlayerWindow();
            }
        });

        fileMenu.addSeparator();

        JMenuItem optionItem = new JMenuItem("Настройки");
        fileMenu.add(optionItem);
        optionItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPropertyDialog();

            }
        });

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Выйти");
        fileMenu.add(exitItem);

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Попытка корректно заверишть запись, если она велась
                // TODO: Не тестировалось!
                if (world != null && world.isRecording()) {
                    world.stopRecording();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

        menuBar.add(fileMenu);

        JMenu ViewMenu = new JMenu("Вид");
        menuBar.add(ViewMenu);

        JMenuItem item;
        for (int i = 0; i < views.length; i++) {
            item = new JMenuItem(views[i].getName());
            ViewMenu.add(item);
            item.addActionListener(new ViewMenuActionListener(this, views[i]));
        }

        JMenu toolsMenu = new JMenu("Инструменты");
        menuBar.add(toolsMenu);

//        JMenuItem saveWorldItem = new JMenuItem("Сохранить мир");
//        toolsMenu.add(saveWorldItem);
//        saveWorldItem.addActionListener((ActionEvent e) -> {
//        });
        JMenuItem loadWorldItem = new JMenuItem("Загрузить мир");
        toolsMenu.add(loadWorldItem);
        loadWorldItem.addActionListener((ActionEvent e) -> {
            class FrameExtensionFilter extends FileFilter {
                private static final String SUFFIX = ".frame.cb.zip";
                private final String description;
                public FrameExtensionFilter(String description) {
                    this.description = description;
                };
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(SUFFIX);
                }
                @Override
                public String getDescription() {
                    return description;
                }
            }
            FrameExtensionFilter filter = new FrameExtensionFilter(
                    "Сохраненный мир (*.frame.cb.zip)");
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(filter);
            if (fc.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                if (world == null) {
                    int width = paintPanel.getWidth() / BOTW;// Ширина доступной части экрана для рисования карты
                    int height = paintPanel.getHeight() / BOTH;// Боты 4 пикселя?
                    world = new World(window, width, height);
                }
                world.openFile(fc.getSelectedFile());
            }
        });

        this.setJMenuBar(menuBar);

        view = new ViewBasic();
        this.pack();
        this.setVisible(true);
        setExtendedState(NORMAL);

        String tmp = this.getFileDirectory();
        if (tmp == null || tmp.length() == 0) {
            showPropertyDialog();
        }
    }

    void showPropertyDialog() {
        JTextField fileDirectoryName = new JTextField();
        fileDirectoryName.setText(getFileDirectory());
        final JComponent[] inputs = new JComponent[]{
            new JLabel("Директория для хранения файлов записи"),
            fileDirectoryName,};
        int result = JOptionPane.showConfirmDialog(window, inputs, "Настройки", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
        if (result == JOptionPane.OK_OPTION) {
            window.setFileDirectory(fileDirectoryName.getText());
        }
    }

    protected void setFileDirectory(String name) {
        this.properties.setFileDirectory(name);
    }

    protected String getFileDirectory() {
        return this.properties.getFileDirectory();
    }

    class CustomListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            if (world.started()) {
                return;//Если идет обсчет не суетимся, выводить ничего не надо.
            }
            Point p = e.getPoint();
            int x = (int) p.getX();
            int y = (int) p.getY();
            int botX = (x - 2) / BOTW;
            int botY = (y - 2) / BOTH;
            Bot bot = world.getBot(botX, botY);
            if (bot != null) {
                {
                    Graphics g = buffer.getGraphics();
                    g.setColor(Color.MAGENTA);
                    g.fillRect(botX * BOTW, botY * BOTH, BOTW, BOTH);
//                    g.setColor(Color.BLACK);
                    //                  g.drawRect(botX * 4, botY * 4, 4, 4);
                    paintPanel.repaint();
                }
                StringBuilder buf = new StringBuilder();
                buf.append("<html>");
                buf.append("<p>Многоклеточный: ");
                switch (bot.isMulti()) {
                    case 0:// - нет,
                        buf.append("нет</p>");
                        break;
                    case 1:// - есть MPREV,
                        buf.append("есть MPREV</p>");
                        break;
                    case 2:// - есть MNEXT,
                        buf.append("есть MNEXT</p>");
                        break;
                    case 3:// есть MPREV и MNEXT
                        buf.append("есть MPREV и MNEXT</p>");
                        break;
                }
                buf.append("<p>c_blue=" + bot.c_blue);
                buf.append("<p>c_green=" + bot.c_green);
                buf.append("<p>c_red=" + bot.c_red);
                buf.append("<p>direction=" + bot.direction);
                buf.append("<p>health=" + bot.health);
                buf.append("<p>mineral=" + bot.mineral);

                //buf.append("");
                IBotGeneController cont;
                for (int i = 0; i < Bot.MIND_SIZE; i++) {//15
                    int command = bot.mind[i];  // текущая команда

                    // Получаем обработчика команды
                    cont = Bot.geneController[command];
                    if (cont != null)// если обработчик такой команды назначен
                    {
                        buf.append("<p>");
                        buf.append(String.valueOf(i));
                        buf.append("&nbsp;");
                        buf.append(cont.getDescription(bot, i));
                        buf.append("</p>");
                    }
                }

                buf.append("</html>");
                JComponent component = (JComponent) e.getSource();
                //System.out.println(bot);
                paintPanel.setToolTipText(buf.toString());
                MouseEvent phantom = new MouseEvent(
                        component,
                        MouseEvent.MOUSE_MOVED,
                        System.currentTimeMillis() - 2000,
                        0,
                        x,
                        y,
                        0,
                        false);

                ToolTipManager.sharedInstance().mouseMoved(phantom);
            }

        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }

    @Override
    public void setView(IView view) {
        this.view = view;
    }

    public void paint() {
        buffer = this.view.paint(this.world, this.paintPanel);
        generationLabel.setText(" Generation: " + String.valueOf(world.generation));
        populationLabel.setText(" Population: " + String.valueOf(world.population));
        organicLabel.setText(" Organic: " + String.valueOf(world.organic));
        pestsLabel.setText(" Pests: " + String.valueOf(world.pests));
        pestGenesLabel.setText(" Pest genes: " + String.valueOf(world.pestGenes));
        // переводим время, затраченное на PAINT_STEP пересчетов в пересчеты в секунду
        perfLabel.setText(" WIPS: " + String.format("%3.1f", World.PAINT_STEP / (PerfMeter.getDiff() / 1000000000.0)));
        recorderBufferLabel.setText(" Buffer: " + String.valueOf(world.recorder.getBufferSize()));

        Runtime runtime = Runtime.getRuntime();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        memoryLabel.setText(" Memory MB: " + String.valueOf(memory / (1024L * 1024L)));

        frameSavedCounterLabel.setText(" Saved frames: " + String.valueOf(world.world.recorder.getFrameSavedCounter()));
        frameSkipSizeLabel.setText(" Skip frames: " + String.valueOf(world.world.recorder.getFrameSkipSize()));

        paintPanel.repaint();
    }

    public static void main(String[] args) {
        MainWindow.window = new MainWindow();
    }

    @Override
    public ProjectProperties getProperties() {
        return this.properties;
    }
}
