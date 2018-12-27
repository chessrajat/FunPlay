package com.websbro.funplay;

public class EpisodeDetails {
    String seasonName;
    String episodeNumber;
    String episodeLink;
    String season;

    public EpisodeDetails(String seasonName, String episodeNumber, String episodeLink, String season) {
        this.seasonName = seasonName;
        this.episodeNumber = episodeNumber;
        this.episodeLink = episodeLink;
        this.season = season;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public String getEpisodeLink() {
        return episodeLink;
    }

    public String getSeason() {
        return season;
    }
}
