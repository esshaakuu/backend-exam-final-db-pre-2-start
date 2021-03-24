package com.codecool.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RadioCharts {

    private final String url;
    private final String user;
    private final String password;

    public RadioCharts(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public String getMostPlayedSong() {
        List<Song> songs = new ArrayList<>();
        String SQL = "SELECT song, times_aired FROM music_broadcast";

        try (Connection connection = getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(SQL);

            while (resultSet.next()) {
                String songName = resultSet.getString(1);
                Integer timesAired = resultSet.getInt(2);
                songs.add(new Song(songName, timesAired));
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return getSongWithMaxAirTime(songs);
    }

    public String getSongWithMaxAirTime(List<Song> songs) {
        List<Song> result = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            boolean hasChanged = false;
            for (int j = 0; j < result.size(); j++) {
                if (result.get(j).equals(songs.get(i))) {
                    result.get(j).addAirTime(songs.get(i).getTimesAired());
                    hasChanged = true;
                    break;
                }
            }
            if (!hasChanged) result.add(songs.get(i));
        }
        return selectSongWithMaxAirTime(result);
    }

    public String selectSongWithMaxAirTime(List<Song> songs) {
        int maxAirTimeIndex = 0;
        for (int i = 1; i < songs.size(); i++) {
            if (songs.get(maxAirTimeIndex).getTimesAired() < songs.get(i).getTimesAired()) {
                maxAirTimeIndex = i;
            }
        }
        return songs.size() > 0 ? songs.get(maxAirTimeIndex).getTitle() : "";
    }

    public String getMostActiveArtist() {
        List<Artist> artists = new ArrayList<>();
        String SQL = "SELECT artist, sum(times_aired) FROM music_broadcast group by artist";

        try (Connection connection = getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(SQL);

            while (resultSet.next()) {
                String artistName = resultSet.getString(1);
                Integer airTime = resultSet.getInt(2);
                artists.add(new Artist(artistName, airTime));
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return selectArtistByMaxAirTime(artists);
    }

//    public String getArtistOccurrenceList( List<Artist> artists) {
//        List<Artist> result = new ArrayList<>();
//        for (int i = 0; i < artists.size(); i++) {
//            boolean hasChanged = false;
//            for (int j = 0; j < result.size(); j++) {
//                if (result.get(j).getName().equals(artists.get(i).getName())) {
//                    result.get(j).addOccurrence();
//                    hasChanged = true;
//                    break;
//                }
//            }
//            if (!hasChanged) result.add(clubs.get(i));
//        }
//        return selectClubByMaxPoints(result);
//    }

    public String selectArtistByMaxAirTime(List<Artist> artists) {
        int maxAirTimeIndex=0;
        for (int i = 1; i < artists.size(); i++) {
            if (artists.get(maxAirTimeIndex).getAirTime() < artists.get(i).getAirTime()) {
                 maxAirTimeIndex = i;
            }
        }
        return artists.size() > 0 ? artists.get(maxAirTimeIndex).getName() : "";
    }
}
