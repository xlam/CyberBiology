package ru.cyberbiology.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import ru.cyberbiology.BasicBot;
import ru.cyberbiology.BasicWorld;
import ru.cyberbiology.Bot;

/**
 * Класс управляет процессом сохранения и загрузки снимков мира.
 *
 * @author Nickolay, Sergey Sokolov
 */
public class SnapshotManager {

    public static final int BOT_DATA_LENGTH = 14 + BasicBot.MIND_SIZE;

    private static final int VERSION = 0;
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
    private final ProjectProperties properties = ProjectProperties.getInstance();

    /**
     * Сохраняет снимок мира.
     *
     * @param world сохраняемый мир
     */
    public void saveSnapshot(BasicWorld world) {

        String dirName = properties.getFileDirectory();
        new File(dirName).mkdirs();

        //Создаем временный файл с данными
        String fileName = FORMATTER.format(new Date()) + "";
        File file = new File(dirName + fileName + ".frame.cb.zip");

        try (ZipOutputStream fileout = new ZipOutputStream(new FileOutputStream(file));
                DataOutputStream out = new DataOutputStream(fileout)) {

            fileout.putNextEntry(new ZipEntry("data"));
            // Версия
            out.writeInt(VERSION);
            int width = world.getWidth();
            // Ширина мира
            out.writeInt(width);
            int height = world.getHeight();
            // Высота мира
            out.writeInt(height);

            SnapShotFrame frame = new SnapShotFrame();
            BasicBot bot;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bot = world.getBot(x, y);
                    if (bot != null) {
                        frame.addBot(bot);
                    }
                }
            }
            frame.save(out);
            out.writeInt(0);    // Следующий фрейм размером 0 - больше данных нет, конец записи
            fileout.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Загружает снимок мира.
     *
     * @param world мир, в который будет загружен снимок
     * @param file файл снимка
     */
    public void loadSnapshot(BasicWorld world, File file) {
        if (file.getName().endsWith(".frame.cb.zip")) {

            try (ZipInputStream filein = new ZipInputStream((new FileInputStream(file)));
                    DataInputStream in = new DataInputStream(filein)) {

                ZipEntry zipEntry = filein.getNextEntry();
                int version = in.readInt();
                if (version != VERSION) {
                    System.err.println("WARNING: Snapshot '" + file.getName() + "' was made by another application version!");
                    System.err.println("WARNING: There is no guarantee it loads correctly!");
                }
                int width = in.readInt();
                int height = in.readInt();
                world.setSize(width, height);

                int frameLength = in.readInt();
                if (frameLength == 0) {
                    return;
                }

                int i = 0;
                while (i < frameLength) {
                    BasicBot bot = new BasicBot((BasicWorld) world);
                    bot.adr = in.readByte();        //data[0]
                    bot.posX = in.readInt();           //data[1]
                    bot.posY = in.readInt();           //data[2]=
                    bot.health = in.readInt();      //data[3]=
                    bot.mineral = in.readInt();     //data[4]=
                    bot.alive = in.readByte();      //data[5]=
                    bot.colorRed = in.readInt();       //data[6]=
                    bot.colorGreen = in.readInt();     //data[7]=
                    bot.colorBlue = in.readInt();      //data[8]=
                    bot.direction = in.readByte();  //data[9]=
                    bot.mprevX = in.readInt();      //data[10]
                    bot.mprevY = in.readInt();      //data[11]
                    bot.mnextX = in.readInt();      //data[12]
                    bot.mnextY = in.readInt();      //data[13]
                    i += 14;

                    for (int m = 0; m < bot.mind.length; m++) {
                        bot.mind[m] = (byte) in.readByte();
                        i++;
                    }
                    world.setBot(bot);
                }
                world.restoreLinks();
                world.restoreStats();
                world.paint();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SnapShotFrame {

        private final List<Item> list;

        SnapShotFrame() {
            list = new ArrayList<>();
        }

        public int save(DataOutputStream fileout) throws IOException {
            int length = list.size() * BOT_DATA_LENGTH;
            fileout.writeInt(length);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).save(fileout);
            }
            return length;
        }

        public void addBot(Bot bot) {
            this.list.add(new Item((BasicBot) bot));
        }
    }

    private class Item {

        private final byte botAdr;
        private final int botX;
        private final int botY;
        private final int botHealth;
        private final int botMineral;
        private final byte botAlive;
        private final int botColorRed;
        private final int botColorGreen;
        private final int botColorBlue;
        private final byte botDirection;
        private final int botMprevX;
        private final int botMprevY;
        private final int botMnextX;
        private final int botMnextY;
        private final byte[] mind;

        Item(BasicBot bot) {
            // жестко сохраняем все зхначения, так как к моменту сохранения кадра данные могут изменится
            botAdr = (byte) bot.adr;
            botX = bot.posX;
            botY = bot.posY;
            botHealth = bot.health;
            botMineral = bot.mineral;
            botAlive = (byte) bot.alive;
            botColorRed = bot.colorRed;
            botColorGreen = bot.colorGreen;
            botColorBlue = bot.colorBlue;
            botDirection = (byte) bot.direction;

            if (bot.mprev != null) {
                botMprevX = bot.mprev.posX;
                botMprevY = bot.mprev.posY;
            } else {
                botMprevX = botMprevY = -1;
            }
            if (bot.mnext != null) {
                botMnextX = bot.mnext.posX;
                botMnextY = bot.mnext.posY;
            } else {
                botMnextX = botMnextY = -1;
            }
            mind = new byte[bot.mind.length];
            System.arraycopy(bot.mind, 0, mind, 0, bot.mind.length);
        }

        public void save(DataOutputStream fileout) throws IOException {
            fileout.writeByte(botAdr);
            fileout.writeInt(botX);
            fileout.writeInt(botY);
            fileout.writeInt(botHealth);
            fileout.writeInt(botMineral);
            fileout.writeByte(botAlive);
            fileout.writeInt(botColorRed);
            fileout.writeInt(botColorGreen);
            fileout.writeInt(botColorBlue);
            fileout.writeByte(botDirection);
            fileout.writeInt(botMprevX);
            fileout.writeInt(botMprevY);
            fileout.writeInt(botMnextX);
            fileout.writeInt(botMnextY);
            for (int i = 0; i < mind.length; i++) {
                fileout.writeByte(mind[i]);
            }
        }
    }
}
