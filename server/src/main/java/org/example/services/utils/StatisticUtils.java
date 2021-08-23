package org.example.services.utils;


import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.example.models.player.User;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class StatisticUtils {
    private static final String[] HEADING = {"ID", "Nickname", "Total games", "Number of wins", "Number of losses", "Number of games for white", "Number of games for black", "Wins against"};
    private static final char DEFAULT_SEPARATOR = ';';

    private StatisticUtils() {
    }

    public static void saveStatistic(final List<User> users, final String path) throws ServerException {
        if (users == null) {
            throw new ServerException(GameErrorCode.PLAYER_NOT_FOUND);
        }
        if (path == null) {
            throw new ServerException(GameErrorCode.FILE_PATH_INVALID);
        }

        try (final FileWriter writer = new FileWriter(path)) {
            final List<String[]> list = new ArrayList<>();
            list.add(HEADING);
            list.addAll(playersToListString(users));

            for (final String[] m : list) {
                for (final String s : m) {
                    writer.append(s).append(DEFAULT_SEPARATOR);
                }
                writer.append(CSVWriter.DEFAULT_LINE_END);
            }
        } catch (final IOException e) {
            log.error("Writing to the CSV file is not successful {} {}", path, e.getMessage());
            throw new ServerException(GameErrorCode.SAVE_FILE_ERROR);
        }
    }

    private static List<String[]> playersToListString(final List<User> users) {
        final List<String[]> list = new ArrayList<>();
        for (final User user : users) {
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
        final StringBuilder str = new StringBuilder();
        for (final Map.Entry<User, Integer> entry : user.getStatistics().getWinsAgainst().entrySet()) {
            str.append(entry.getKey().getNickname());
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
