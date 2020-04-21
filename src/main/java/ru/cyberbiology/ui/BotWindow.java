package ru.cyberbiology.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import ru.cyberbiology.BasicBot;
import ru.cyberbiology.gene.Gene;
import ru.cyberbiology.util.MiscUtils;

/**
 * Окно информации о боте.
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public class BotWindow extends JFrame {

    // updatable fields
    private final JLabel jlAddress = new JLabel("-");
    private final JLabel jlEnergy = new JLabel("-");
    private final JLabel jlMinerals = new JLabel("-");
    private final JLabel jlPosX = new JLabel("-");
    private final JLabel jlPosY = new JLabel("-");
    private final JLabel jlState = new JLabel("-");
    // html нужен для переноса строк длинных описаний
    private final JLabel jlControllerDescription = new JLabel("<html><br /><br /></html");

    private final JList<String> jlGenesHistory = new JList<>();
    private final JList<String> jlProgram = new JList<>();

    private final JScrollPane historyPane = new JScrollPane();
    private final JScrollPane programPane = new JScrollPane();
    private final JPanel infoPane = new JPanel();
    private final JPanel genomePane = new JPanel();

    private final JLabel[] genome = new JLabel[BasicBot.MIND_SIZE];

    private final BasicBot bot;

    /**
     * Создает окно информации о боте.
     *
     * @param bot бот
     */
    public BotWindow(BasicBot bot) {
        super(bot.toString());
        this.bot = bot;
        initComponents();
        pack();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setSize(200, 200);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setupHistoryAndProgramPanes();
        setupInfoPane();
        setupGenomePane();

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(4, 4, 4, 2);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 2;
        add(historyPane, gbc);

        gbc.insets = new Insets(4, 2, 4, 2);
        gbc.weightx = 0;
        gbc.gridx = 1;
        add(programPane, gbc);

        gbc.insets = new Insets(4, 2, 2, 4);
        gbc.gridheight = 1;
        gbc.weighty = 0;
        gbc.gridx = 2;
        add(infoPane, gbc);

        gbc.insets = new Insets(2, 2, 4, 4);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.gridy = 1;
        add(genomePane, gbc);
    }

    private void setupHistoryAndProgramPanes() {
        historyPane.setBorder(BorderFactory.createTitledBorder("История генов"));
        historyPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        historyPane.setViewportView(jlGenesHistory);
        historyPane.setPreferredSize(new Dimension(200, HEIGHT));
        programPane.setBorder(BorderFactory.createTitledBorder("Пр."));
        programPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        programPane.setViewportView(jlProgram);
        programPane.setMinimumSize(new Dimension(50, HEIGHT));
        programPane.setPreferredSize(programPane.getMinimumSize());
    }

    private void setupInfoPane() {
        infoPane.setBorder(BorderFactory.createTitledBorder("Параметры бота"));
        infoPane.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel jlStateLabel = new JLabel("Состояние:");
        JLabel jlEnergyLable = new JLabel("Энергия:");
        JLabel jlMineralsLabel = new JLabel("Минералы:");
        JLabel jlAddressLable = new JLabel("УТК:");
        JLabel jlPosXLabel = new JLabel("X:");
        JLabel jlPosYLabel = new JLabel("Y:");

        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.3;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPane.add(jlStateLabel, gbc);
        gbc.gridy = 1;
        infoPane.add(jlEnergyLable, gbc);
        gbc.gridy = 2;
        infoPane.add(jlMineralsLabel, gbc);
        gbc.gridy = 3;
        infoPane.add(jlAddressLable, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.2;
        gbc.ipadx = 50;
        gbc.gridx = 1;
        gbc.gridy = 0;
        infoPane.add(jlState, gbc);
        gbc.gridy = 1;
        infoPane.add(jlEnergy, gbc);
        gbc.gridy = 2;
        infoPane.add(jlMinerals, gbc);
        gbc.gridy = 3;
        infoPane.add(jlAddress, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.3;
        gbc.ipadx = 0;
        gbc.gridx = 2;
        gbc.gridy = 0;
        infoPane.add(jlPosXLabel, gbc);
        gbc.gridy = 1;
        infoPane.add(jlPosYLabel, gbc);
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.2;
        gbc.ipadx = 30;
        gbc.gridx = 3;
        gbc.gridy = 0;
        infoPane.add(jlPosX, gbc);
        gbc.gridy = 1;
        infoPane.add(jlPosY, gbc);
    }

    private void setupGenomePane() {
        genomePane.setBorder(BorderFactory.createTitledBorder("Геном"));
        genomePane.setLayout(new GridBagLayout());

        JPanel genomeTable = new JPanel(new GridLayout(8, 8, 2, 2));
        Dimension d = new Dimension(20, 20); // размер ячейки таблицы

        JLabel l;
        for (int i = 0; i < BasicBot.MIND_SIZE; i++) {
            l = new JLabel(String.valueOf(i));
            l.setPreferredSize(d);
            l.setVerticalAlignment(SwingConstants.CENTER);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setOpaque(true);
            l.setBackground(Color.WHITE);
            genome[i] = l;
            genomeTable.add(l);
        }
        genomeTable.setMinimumSize(getSize());
        genomeTable.setPreferredSize(getSize());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;
        genomePane.add(genomeTable, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 1;
        gbc.gridy = 1;
        genomePane.add(jlControllerDescription, gbc);
    }

    /**
     * Обновляет информацию о боте.
     */
    public void update() {

        // общая информация
        jlState.setText(String.valueOf(bot.alive));
        jlEnergy.setText(String.valueOf(bot.health));
        jlMinerals.setText(String.valueOf(bot.mineral));
        jlAddress.setText(String.valueOf(bot.adr));
        jlPosX.setText(String.valueOf(bot.posX));
        jlPosY.setText(String.valueOf(bot.posY));

        // история выполненных генов
        String[] history = bot.genesHistory.toStringArray();
        // TODO это не годится, нужно переделать!
        jlGenesHistory.setListData(Stream.of(history)
            .map(g -> {
                Gene gene = bot.getGeneById(Integer.parseInt(g));
                return g + " [" + ((gene == null) ? "не назначено" : gene.getDescription()) + "]";
            })
            .toArray(String[]::new)
        );

        // программа
        String[] program = MiscUtils.getProgram(history);
        if (program != null) {
            jlProgram.setListData(program);
        }

        // таблица генов
        JLabel l;
        for (int i = 0; i < BasicBot.MIND_SIZE; i++) {
            l = genome[i];
            l.setText(String.valueOf(bot.mind[i]));
            l.setBackground((i == bot.adr) ? Color.RED : Color.WHITE);
        }

        // описание текущей команды
        int id = bot.mind[bot.adr];
        Gene gene = bot.getGeneById(id);
        String c = "<html>[" + id + "]"
                + ((gene == null) ? " не назначено" : " " + gene.getDescription())
                + "</html>";
        jlControllerDescription.setText(c);
    }

    /**
     * Отображает окно бота.
     */
    public void showWindow() {
        pack();
        update();
        setVisible(true);
    }
}
