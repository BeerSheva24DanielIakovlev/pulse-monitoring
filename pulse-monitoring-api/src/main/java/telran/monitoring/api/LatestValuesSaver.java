package telran.monitoring.api;

import java.util.List;

public interface LatestValuesSaver {
    void addValue(SensorData sensorData);
    List <SensorData> getAllVAlues (long patientId);
    SensorData getLastValue(long patientId);
    void clearValues(long patientId);
    void clearAndAddValue(long patientId, SensorData sensorData);
}
