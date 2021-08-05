package services.utils;


import com.opencsv.CSVWriter;
import exception.GameErrorCode;
import exception.GameException;
import lombok.extern.slf4j.Slf4j;
import models.player.User;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class StatisticUtils {
    private static final String[] HEADING = {"ID", "Nickname", "Total games", "Number of wins", "Number of losses", "Number of games for white", "Number of games for black", "Wins against"};
    private static final char DEFAULT_SEPARATOR = ';';

    public static void saveStatistic(final List<User> users, final String path) throws GameException {
        if (users == null) {
            throw new GameException(GameErrorCode.PLAYER_NOT_FOUND);
        }
        if (path == null) {
            throw new GameException(GameErrorCode.FILE_PATH_INVALID);
        }

        try (FileWriter writer = new FileWriter(path)) {
            List<String[]> list = new ArrayList<>();
            list.add(HEADING);
            list.addAll(playersToListString(users));

            for (String[] m : list) {
                for (String s : m) {
                    writer.append(s).append(DEFAULT_SEPARATOR);
                }
                writer.append(CSVWriter.DEFAULT_LINE_END);
            }
        } catch (IOException e) {
            log.error("Writing to the CSV file is not successful {} {}", path, e.getMessage());
            throw new GameException(GameErrorCode.SAVE_FILE_ERROR);
        }
    }

    private static List<String[]> playersToListString(final List<User> users) {
        List<String[]> list = new ArrayList<>();
        for (User user : users) {
            list.add(playerToString(user));
        }
        return list;
    }

    private static String[] playerToString(final User user) {
        return new String[]{String.valueOf(user.getId()),
                user.getNickname(),
                String.valueOf(user.getStatistics().getTotalGames()),
                String.valueOf(user.getStatistics().getWin()),
                String.valueOf(user.getStatistics().getLose()),
                String.valueOf(user.getStatistics().getPlayBlackColor()),
                String.valueOf(user.getStatistics().getPlayWhiteColor()),
                listAgainst(user)
        };
    }

    private static String listAgainst(final User user) {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Integer> entry : user.getStatistics().getWinsAgainst().entrySet()) {
            str.append(entry.getKey());
            str.append(" - ");
            str.append(entry.getValue());
            str.append("\n");
            for (int i = 0; i < HEADING.length - 1; i++) {
                str.append(DEFAULT_SEPARATOR);
            }
        }
        if (str.length() > 0) {
            str.delete(str.length() - HEADING.length, str.length());
        }
        return str.toString();
    }
}
