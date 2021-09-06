package profile;

import parser.LogParser;

import java.io.*;

public class ProfileService {
    public static Profile fromFile(final String fileName) throws Exception {
        try (final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Profile) ois.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            throw new Exception("Не удалось считать из профиль из файла.");
        }
    }

    public static void save(final Profile profile, final String fileName) throws Exception {
        try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(profile);
        } catch (final IOException e) {
            throw new Exception("Не удалось сохранить профиль в файл.");
        }
    }

    public static Profile parse(final String logPath) {
        final LogParser logParser = new LogParser();
        return logParser.parseProfile(logPath);
    }
}
