package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Track;

import java.util.List;

public interface TrackDao {

    Integer createTrack(Track track);

    List<Track> getTracksByTeamId(Integer teamId);
}
