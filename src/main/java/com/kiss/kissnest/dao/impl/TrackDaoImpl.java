package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.TrackDao;
import com.kiss.kissnest.entity.Track;
import com.kiss.kissnest.mapper.TrackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrackDaoImpl implements TrackDao {

    @Autowired
    private TrackMapper trackMapper;

    @Override
    public Integer createTrack(Track track) {

        return trackMapper.createTrack(track);
    }

    @Override
    public List<Track> getTracksByTeamId(Integer teamId) {

        return trackMapper.getTracksByTeamId(teamId);
    }
}
