package com.siemens.trainticketing.repository;

import com.siemens.trainticketing.entity.Train;
import com.siemens.trainticketing.entity.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {
    List<TrainSchedule> findByTrainIdOrderByStopOrderAsc(Long trainId);

    List<TrainSchedule> findByStationId(Long stationId);

    TrainSchedule findByTrainIdAndStationId(Long trainId, Long stationId);
}
