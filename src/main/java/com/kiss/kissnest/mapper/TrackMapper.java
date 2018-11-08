package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Track;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TrackMapper {

    Integer createTrack(Track track);

    List<Track> getTracksByTeamId(Integer teamId);
}
