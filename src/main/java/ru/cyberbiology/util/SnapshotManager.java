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
 *
 * @author Nickolay, Sergey Sokolov
 */
public class SnapshotManager {

    private static final int VERSION = 0;
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
    public static final int BOT_DATA_LENGTH = 14 + BasicBot.MIND_SIZE;

    public void saveSnapshot(BasicWorld world) {

        String dirName = world.getProperties().getFileDirectory();
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
     * Загрузка снимка мира.
     * @param world мир, в которые будет загружен снимок
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
                    bot.x = in.readInt();           //data[1]
                    bot.y = in.readInt();           //data[2]=
                    bot.health = in.readInt();      //data[3]=
                    bot.mineral = in.readInt();     //data[4]=
                    bot.alive = in.readByte();      //data[5]=
                    bot.c_red = in.readInt();       //data[6]=
                    bot.c_green = in.readInt();     //data[7]=
                    bot.c_blue = in.readInt();      //data[8]=
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

    private class SnapShotFrame implements Frame {

        private final List<Item> list;

        SnapShotFrame() {
            list = new ArrayList<>();
        }

        @Override
        public int save(DataOutputStream fileout) throws IOException {
            int length = list.size() * BOT_DATA_LENGTH;
            fileout.writeInt(length);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).save(fileout);
            }
            return length;
        }

        @Override
        public void addBot(Bot bot) {
            this.list.add(new Item((BasicBot) bot));
        }
    }

    private class Item {

        private final byte bot_adr;
        private final int bot_x;
        private final int bot_y;
        private final int bot_health;
        private final int bot_mineral;
        private final byte bot_alive;
        private final int bot_c_red;
        private final int bot_c_green;
        private final int bot_c_blue;
        private final byte bot_direction;
        private final int bot_mprev_x;
        private final int bot_mprev_y;
        private final int bot_mnext_x;
        private final int bot_mnext_y;
        private final byte[] mind;

        Item(BasicBot bot) {
            // жестко сохраняем все зхначения, так как к моменту сохранения кадра данные могут изменится
            bot_adr = (byte) bot.adr;
            bot_x = bot.x;
            bot_y = bot.y;
            bot_health = bot.health;
            bot_mineral = bot.mineral;
            bot_alive = (byte) bot.alive;
            bot_c_red = bot.c_red;
            bot_c_green = bot.c_green;
            bot_c_blue = bot.c_blue;
            bot_direction = (byte) bot.direction;

            if (bot.mprev != null) {
                bot_mprev_x = bot.mprev.x;
                bot_mprev_y = bot.mprev.y;
            } else {
                bot_mprev_x = bot_mprev_y = -1;
            }
            if (bot.mnext != null) {
                bot_mnext_x = bot.mnext.x;
                bot_mnext_y = bot.mnext.y;
            } else {
                bot_mnext_x = bot_mnext_y = -1;
            }
            mind = new byte[bot.mind.length];
            System.arraycopy(bot.mind, 0, mind, 0, bot.mind.length);
        }

        public void save(DataOutputStream fileout) throws IOException {
            fileout.writeByte(bot_adr);
            fileout.writeInt(bot_x);
            fileout.writeInt(bot_y);
            fileout.writeInt(bot_health);
            fileout.writeInt(bot_mineral);
            fileout.writeByte(bot_alive);
            fileout.writeInt(bot_c_red);
            fileout.writeInt(bot_c_green);
            fileout.writeInt(bot_c_blue);
            fileout.writeByte(bot_direction);
            fileout.writeInt(bot_mprev_x);
            fileout.writeInt(bot_mprev_y);
            fileout.writeInt(bot_mnext_x);
            fileout.writeInt(bot_mnext_y);
            for (int i = 0; i < mind.length; i++) {
                fileout.writeByte(mind[i]);
            }
        }
    }
}
