package com.websbro.funplay;

public class EpisodeDetails {
    String episodeId;
    String seasonName;
    String episodeNumber;
    String episodeLink;
    String season;

    public EpisodeDetails(String seasonName, String episodeNumber, String episodeLink, String season,String episodeId) {
        this.seasonName = seasonName;
        this.episodeNumber = episodeNumber;
        this.episodeLink = episodeLink;
        this.season = season;
        this.episodeId = episodeId;
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

    public String getEpisodeId() {
        return episodeId;
    }
}
