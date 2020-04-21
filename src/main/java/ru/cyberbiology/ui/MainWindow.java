package ru.cyberbiology.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import ru.cyberbiology.BasicBot;
import ru.cyberbiology.BasicWorld;
import ru.cyberbiology.World;
import ru.cyberbiology.util.PerfMeter;
import ru.cyberbiology.util.ProjectProperties;
import ru.cyberbiology.util.SnapshotManager;
import ru.cyberbiology.view.View;
import ru.cyberbiology.view.ViewBasic;
import ru.cyberbiology.view.ViewEnergy;
import ru.cyberbiology.view.ViewMineral;
import ru.cyberbiology.view.ViewMultiCell;
import ru.cyberbiology.view.ViewPest;

public class MainWindow extends JFrame implements Painter {

    /**
     * Буфер для отрисовки ботов.
     */
    public Image buffer = null;

    private BasicWorld world;

    /**
     * Актуальный отрисовщик.
     */
    private View view;

    /**
     * Перечень возможных отрисовщиков.
     */
    private final View[] views = new View[]{
        new ViewBasic(),
        new ViewEnergy(),
        new ViewMineral(),
        new ViewMultiCell(),
        new ViewPest(),
    };

    private final JLabel generationLabel = new JLabel(" Generation: 0 ");
    private final JLabel populationLabel = new JLabel(" Population: 0 ");
    private final JLabel organicLabel = new JLabel(" Organic: 0 ");
    private final JLabel pestsLabel = new JLabel(" Pests: 0 ");
    private final JLabel pestGenesLabel = new JLabel(" Pest genes: 0 ");
    private final JLabel perfLabel = new JLabel(" WIPS: 0 ");
    private final JLabel memoryLabel = new JLabel("");

    private final JButton pauseButton = new JButton();
    private final JButton startButton = new JButton();
    private final JButton doIterationButton = new JButton();

    private final List<BotWindow> botWindows = new ArrayList<>();

    private final JPanel paintPanel = new JPanel() {
        @Override
        public void paint(Graphics g) {
            g.drawImage(buffer, 0, 0, null);
        }
    };

    private final ProjectProperties properties;
    private final SettingsDialog settingsDialog;
    private final SnapshotManager snapshotManager = new SnapshotManager();

    /**
     * Создает главное окно приложения и показывает его на экране.
     */
    public MainWindow() {

        properties = ProjectProperties.getInstance();
        settingsDialog = new SettingsDialog(this, true);

        setTitle("CyberBiology " + getVersionFromProperties());
        setPreferredSize(new Dimension(900, 700));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try {
            setIconImage(ImageIO.read(getClass().getResource("/icons/cb-app-03.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // у этого лейаута приятная особенность - центральная часть растягивается автоматически
        setLayout(new BorderLayout());

        setupMenuBar();
        setupToolBar();
        setupPaintPanel();
        setupStatusPanel();

        view = new ViewBasic();
        pack();
        setVisible(true);
        setExtendedState(NORMAL);
    }

    private void showSaveDirectoryDialog() {
        JTextField fileDirectoryName = new JTextField();
        fileDirectoryName.setText(getFileDirectory());
        final JComponent[] inputs = new JComponent[]{
            new JLabel("Директория для хранения файлов записи"),
            fileDirectoryName,};
        int result = JOptionPane.showConfirmDialog(this, inputs, "Установки", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
        if (result == JOptionPane.OK_OPTION) {
            setFileDirectory(fileDirectoryName.getText());
        }
    }

    private void setFileDirectory(String name) {
        properties.setFileDirectory(name);
    }

    private String getFileDirectory() {
        return properties.getFileDirectory();
    }

    private void setView(View view) {
        this.view = view;
        if (null != world && !world.started()) {
            paint();
        }
    }

    @Override
    public void setWorld(World world) {
        this.world = (BasicWorld) world;
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

        Runtime runtime = Runtime.getRuntime();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        memoryLabel.setText(" Memory MB: " + String.valueOf(memory / (1024L * 1024L)));

        paintPanel.repaint();

        for (BotWindow botWindow : botWindows) {
            botWindow.update();
        }
    }

    private void setupPaintPanel() {
        add(paintPanel, BorderLayout.CENTER); // добавляем нашу карту в центр
        paintPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (world == null) {
                        return;
                    }
                    if (world.started()) {
                        return;//Если идет обсчет не суетимся, выводить ничего не надо.
                    }
                    Point p = e.getPoint();
                    int x = (int) p.getX();
                    int y = (int) p.getY();
                    int botX = (x - 2) / properties.botSize();
                    int botY = (y - 2) / properties.botSize();
                    BasicBot bot = world.getBot(botX, botY);
                    if (bot == null) {
                        return;
                    }
                    BotWindow botWindow = new BotWindow(bot);
                    botWindow.showWindow();
                    botWindow.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            botWindows.remove(botWindow);
                        }
                    });
                    botWindows.add(botWindow);
                    Graphics g = buffer.getGraphics();
                    g.setColor(Color.MAGENTA);
                    g.fillRect(botX * properties.botSize(), botY * properties.botSize(), properties.botSize(), properties.botSize());
                    paintPanel.repaint();
                }
            }
        );
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenu viewMenu = new JMenu("Вид");
        JMenu worldEventsMenu = new JMenu("События");
        JMenu paintStepMenu = new JMenu("Шаг отрисовки");
        JMenu botSizeMenu = new JMenu("Размер бота");
        JMenu settingsMenu = new JMenu("Настройки");
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(worldEventsMenu);
        menuBar.add(settingsMenu);

        JMenuItem restartItem = new JMenuItem("Перезапуск");
        restartItem.addActionListener((ActionEvent e) -> {
            if (world == null) {
                return;
            }
            createWorld();
            world.generateAdam();
            world.start();
            pauseButton.setEnabled(true);
            startButton.setEnabled(false);
            doIterationButton.setEnabled(false);
        });

        JMenuItem mutateItem = new JMenuItem("Cлучайная мутация");
        mutateItem.addActionListener((ActionEvent e) -> {
            if (world != null) {
                world.randomMutation(10, 32);
            }
        });

        JMenuItem adressJumpItem = new JMenuItem("Сбой программы генома");
        adressJumpItem.addActionListener((ActionEvent e) -> {
            if (world != null) {
                world.setRandomCmdAdress();
            }
        });

        JMenuItem snapShotItem = new JMenuItem("Сделать снимок");
        snapShotItem.addActionListener((ActionEvent e) -> {
            if (world != null) {
                world.stop();
                pauseButton.setEnabled(false);
                startButton.setEnabled(true);
                doIterationButton.setEnabled(true);
                snapshotManager.saveSnapshot(world);
            }
        });

        JMenuItem savePathItem = new JMenuItem("Каталог сохранения");
        savePathItem.addActionListener((ActionEvent e) -> {
            showSaveDirectoryDialog();
        });

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        JMenuItem loadWorldItem = new JMenuItem("Загрузить снимок мира");
        loadWorldItem.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileFilter() {
                private static final String SUFFIX = ".frame.cb.zip";

                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(SUFFIX);
                }

                @Override
                public String getDescription() {
                    return "Сохраненный мир (*" + SUFFIX + ")";
                }
            });
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                createWorld();
                snapshotManager.loadSnapshot(world, fc.getSelectedFile());
            }
        });

        JMenuItem settingsDialogItem = new JMenuItem("Параметры");
        settingsDialogItem.addActionListener((ActionEvent e) -> {
            settingsDialog.showSettingsDialog();
        });

        fileMenu.add(restartItem);
        fileMenu.add(snapShotItem);
        fileMenu.add(loadWorldItem);
        fileMenu.addSeparator();
        fileMenu.add(savePathItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        worldEventsMenu.add(mutateItem);
        worldEventsMenu.add(adressJumpItem);
        settingsMenu.add(paintStepMenu);
        settingsMenu.add(botSizeMenu);
        settingsMenu.addSeparator();
        settingsMenu.add(settingsDialogItem);

        /**
         * Меню выбора вида.
         */
        ButtonGroup viewGroup = new ButtonGroup();
        for (View v: views) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(v.getName(), v instanceof ViewBasic);
            item.addActionListener(e -> setView(v));
            viewGroup.add(item);
            viewMenu.add(item);
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

        /**
         * Меню выбора размера бота.
         */
        ButtonGroup botSizeGroup = new ButtonGroup();
        // todo: заменить массив на property
        int[] botSizeValues = {2, 4, 6};
        int currentItem = properties.botSize();
        for (int size : botSizeValues) {
            JRadioButtonMenuItem i = new JRadioButtonMenuItem(String.valueOf(size), currentItem == size);
            i.addActionListener(e -> {
                properties.setProperty("botSize", e.getActionCommand());
            });
            botSizeGroup.add(i);
            botSizeMenu.add(i);
        }

        setJMenuBar(menuBar);
    }

    private void createWorld() {
        if (world != null) {
            world.stop();
        }
        int width = paintPanel.getWidth() / properties.botSize();
        int height = paintPanel.getHeight() / properties.botSize();
        world = new BasicWorld(this, width, height);
        paint();
    }

    private void setupToolBar() {

        pauseButton.setIcon(new ImageIcon(getClass().getResource("/icons/icon-pause-16.png")));
        pauseButton.setMargin(new Insets(0, 0, 0, 0));
        pauseButton.setToolTipText("Пауза");
        pauseButton.addActionListener(e -> {
            world.stop();
            doIterationButton.setEnabled(true);
            pauseButton.setEnabled(false);
            startButton.setEnabled(true);
        });
        pauseButton.setEnabled(false);

        startButton.setIcon(new ImageIcon(getClass().getResource("/icons/icon-start-16.png")));
        startButton.setMargin(new Insets(0, 0, 0, 0));
        startButton.setToolTipText("Запустить/продолжить");
        startButton.addActionListener(e -> {
            if (world == null) {
                createWorld();
                world.generateAdam();
                paint();
            }
            if (!world.started()) {
                world.start();
                doIterationButton.setEnabled(false);
                pauseButton.setEnabled(true);
                startButton.setEnabled(false);
            }
        });

        doIterationButton.setIcon(new ImageIcon(getClass().getResource("/icons/icon-step1-16.png")));
        doIterationButton.setMargin(new Insets(0, 0, 0, 0));
        doIterationButton.setToolTipText("Выполнить один пересчет мира");
        doIterationButton.addActionListener(e -> world.doIteration());
        doIterationButton.setEnabled(false);

        JToolBar toolBar = new JToolBar("Инструменты");

        toolBar.add(pauseButton);
        toolBar.add(startButton);
        toolBar.add(doIterationButton);
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.NORTH);
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

        add(statusPanel, BorderLayout.SOUTH);
    }

    private String getVersionFromProperties() {
        String version = "";
        String propertiesName = "/git.properties";
        InputStream propertiesStream = getClass().getResourceAsStream(propertiesName);
        if (null != propertiesStream) {
            Properties p = new Properties();
            try {
                p.load(propertiesStream);
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            version = p.getProperty("git.commit.id.describe.regex");
            if (p.getProperty("git.closest.tag.name").isEmpty()) {
                version = p.getProperty("git.build.version") + "-" + version;
            }
        }
        return version;
    }
}
