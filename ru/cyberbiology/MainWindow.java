package ru.cyberbiology;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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

    public static final int BOTW = 4;
    public static final int BOTH = 4;

    public static World world;

    /**
     * буфер для отрисовки ботов
     */
    public Image buffer = null;

    /**
     * актуальный отрисовщик
     */
    private IView view;

    /**
     * Перечень возможных отрисовщиков
     */
    private final IView[] views = new IView[]{
        new ViewBasic(),
        new ViewEnergy(),
        new ViewMultiCell(),
        new ViewPest(),
    };

    private final JLabel generationLabel = new JLabel(" Generation: 0 ");
    private final JLabel populationLabel = new JLabel(" Population: 0 ");
    private final JLabel organicLabel = new JLabel(" Organic: 0 ");
    private final JLabel perfLabel = new JLabel(" WIPS: 0 ");
    private final JLabel pestsLabel = new JLabel(" Pests: 0 ");
    private final JLabel pestGenesLabel = new JLabel(" Pest genes: 0 ");
    private final JLabel recorderBufferLabel = new JLabel("");
    private final JLabel memoryLabel = new JLabel("");
    private final JLabel frameSavedCounterLabel = new JLabel("");
    private final JLabel frameSkipSizeLabel = new JLabel("");

    private final JPanel paintPanel = new JPanel() {
        @Override
        public void paint(Graphics g) {
            g.drawImage(buffer, 0, 0, null);
        }
    };

    private final ProjectProperties properties;

    public MainWindow() {
        properties = ProjectProperties.getInstance();
        setTitle("CyberBiologyTest 1.0.0");
        setPreferredSize(new Dimension(1024, 768));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // у этого лейаута приятная особенность - центральная часть растягивается автоматически
        setLayout(new BorderLayout());

        setupStatusPanel();
        setupPaintPanel();
        setupMenuBar();

        view = new ViewBasic();
        pack();
        setVisible(true);
        setExtendedState(NORMAL);

        String tmp = getFileDirectory();
        if (tmp == null || tmp.length() == 0) {
            showPropertyDialog();
        }
    }

    private void showPropertyDialog() {
        JTextField fileDirectoryName = new JTextField();
        fileDirectoryName.setText(getFileDirectory());
        final JComponent[] inputs = new JComponent[]{
            new JLabel("Директория для хранения файлов записи"),
            fileDirectoryName,};
        int result = JOptionPane.showConfirmDialog(this, inputs, "Настройки", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
        if (result == JOptionPane.OK_OPTION) {
            setFileDirectory(fileDirectoryName.getText());
        }
    }

    protected void setFileDirectory(String name) {
        properties.setFileDirectory(name);
    }

    private String getFileDirectory() {
        return properties.getFileDirectory();
    }

    @Override
    public void setView(IView view) {
        this.view = view;
    }

    @Override
    public void paint() {
        buffer = view.paint(world, paintPanel);
        generationLabel.setText(" Generation: " + String.valueOf(world.generation));
        populationLabel.setText(" Population: " + String.valueOf(world.population));
        organicLabel.setText(" Organic: " + String.valueOf(world.organic));
        pestsLabel.setText(" Pests: " + String.valueOf(world.pests));
        pestGenesLabel.setText(" Pest genes: " + String.valueOf(world.pestGenes));
        // переводим время, затраченное на paintstep пересчетов в пересчеты в секунду
        perfLabel.setText(" WIPS: "
                + String.format("%3.1f", Integer.parseInt(properties.getProperty("paintstep")) / (PerfMeter.getDiff() / 1000000000.0)));
        recorderBufferLabel.setText(" Buffer: " + String.valueOf(world.recorder.getBufferSize()));

        Runtime runtime = Runtime.getRuntime();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        memoryLabel.setText(" Memory MB: " + String.valueOf(memory / (1024L * 1024L)));

        frameSavedCounterLabel.setText(" Saved frames: " + String.valueOf(world.world.recorder.getFrameSavedCounter()));
        frameSkipSizeLabel.setText(" Skip frames: " + String.valueOf(world.world.recorder.getFrameSkipSize()));

        paintPanel.repaint();
    }

    @Override
    public ProjectProperties getProperties() {
        return properties;
    }

    private void setupPaintPanel() {
        add(paintPanel, BorderLayout.CENTER); // добавляем нашу карту в центр
        paintPanel.addMouseListener(
            new MouseAdapter() {
                @Override
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
                    if (bot == null) {
                        return;
                    }
                    Graphics g = buffer.getGraphics();
                    g.setColor(Color.MAGENTA);
                    g.fillRect(botX * BOTW, botY * BOTH, BOTW, BOTH);
                    paintPanel.repaint();

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
                    buf.append("<p>c_blue=").append(bot.c_blue);
                    buf.append("<p>c_green=").append(bot.c_green);
                    buf.append("<p>c_red=").append(bot.c_red);
                    buf.append("<p>direction=").append(bot.direction);
                    buf.append("<p>health=").append(bot.health);
                    buf.append("<p>mineral=").append(bot.mineral);

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
        );
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenu viewMenu = new JMenu("Вид");
        JMenu toolsMenu = new JMenu("События");
        JMenu paintStepMenu = new JMenu("Шаг отрисовки");
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(paintStepMenu);

        JMenuItem snapShotItem = new JMenuItem("Сделать снимок");
        JMenuItem runItem = new JMenuItem("Запустить");
        runItem.addActionListener((ActionEvent e) -> {
            if (world == null) {
                // Доступная часть экрана для рисования карты
                int width1 = paintPanel.getWidth() / BOTW;
                int height1 = paintPanel.getHeight() / BOTH;
                world = new World(MainWindow.this, width1, height1);
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
        });

        JMenuItem mutateItem = new JMenuItem("Cлучайная мутация");
        mutateItem.addActionListener((ActionEvent e) -> {
            // мутацию проводим при отключенном мире
            world.stop();
            world.randomMutation(10, 32);
            world.start();
        });

        JMenuItem adressJumpItem = new JMenuItem("Сбой программы генома");
        adressJumpItem.addActionListener((ActionEvent e) -> {
            // TODO переименовать
            world.jumpBotsCmdAdress();
        });

        snapShotItem.addActionListener((ActionEvent e) -> {
            if (world == null) {
                int width1 = paintPanel.getWidth() / BOTW; // Ширина доступной части экрана для рисования карты
                int height1 = paintPanel.getHeight() / BOTH; // Боты 4 пикселя?
                world = new World(MainWindow.this, width1, height1);
                world.generateAdam();
                paint();
            }
            world.stop();
            runItem.setText("Продолжить");
            world.makeSnapShot();
        });

        JMenuItem recordItem = new JMenuItem("Начать запись");
        recordItem.addActionListener((ActionEvent e) -> {
            if (world == null) {
                int width1 = paintPanel.getWidth() / BOTW; // Ширина доступной части экрана для рисования карты
                int height1 = paintPanel.getHeight() / BOTH; // Боты 4 пикселя?
                world = new World(MainWindow.this, width1, height1);
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
        });

        JMenuItem openItem = new JMenuItem("Открыть плеер");
        openItem.addActionListener((ActionEvent e) -> {
            PlayerWindow fw = new PlayerWindow();
        });

        JMenuItem optionItem = new JMenuItem("Настройки");
        optionItem.addActionListener((ActionEvent e) -> {
            showPropertyDialog();
        });

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener((ActionEvent e) -> {
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
        });

        JMenuItem loadWorldItem = new JMenuItem("Загрузить снимок мира");
        loadWorldItem.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileFilter() {
                private static final String SUFFIX = ".frame.cb.zip";
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(SUFFIX);
                }
                @Override
                public String getDescription() {
                    return "Сохраненный мир (*" + SUFFIX + ")";
                }
            });
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (world == null) {
                    int width = paintPanel.getWidth() / BOTW;// Ширина доступной части экрана для рисования карты
                    int height = paintPanel.getHeight() / BOTH;// Боты 4 пикселя?
                    world = new World(this, width, height);
                }
                world.openFile(fc.getSelectedFile());
            }
        });

        fileMenu.add(runItem);
        fileMenu.add(snapShotItem);
        fileMenu.add(loadWorldItem);
        fileMenu.add(recordItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(optionItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        toolsMenu.add(mutateItem);
        toolsMenu.add(adressJumpItem);

        JMenuItem item;
        for (IView v : views) {
            item = new JMenuItem(v.getName());
            viewMenu.add(item);
            item.addActionListener((ActionEvent e) -> setView(v));
        }

        /**
         * Меню выбора шага отрисовки.
         */
        ButtonGroup paintStepGroup = new ButtonGroup();
        // todo: заменить массив на property
        int[] paintStepValues = {10, 500, 1000};
        int currentStep = Integer.parseInt(properties.getProperty("paintstep", "1000"));
        for (int step : paintStepValues) {
            JRadioButtonMenuItem i = new JRadioButtonMenuItem(String.valueOf(step), currentStep == step);
            i.addActionListener(e -> {
                properties.setProperty("paintstep", e.getActionCommand());
            });
            paintStepGroup.add(i);
            paintStepMenu.add(i);
        }

        setJMenuBar(menuBar);
    }

    private void setupStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());

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

        add(statusPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainWindow();
        });
    }
}
