import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * @author atiQue
 * @since 18'Jul 2022 at 9:06 PM
 */

public class WateringTask extends TimerTask {

    private static final double MOISTURE_THRESHOLD = 532;

    private final Pin moistureSensorPin;
    private final Pin mosfetPin;
    private final SSD1306 oledDisplay;

    public WateringTask(Pin moistureSensorPin, Pin mosfetPin, SSD1306 oledDisplay) {
        this.moistureSensorPin = moistureSensorPin;
        this.mosfetPin = mosfetPin;
        this.oledDisplay = oledDisplay;
    }

    @Override
    public void run() {

        StringBuilder logs = new StringBuilder();

        int wateringSeconds = 0;
        String currentDateTime = getCurrentDateTime();
        long currentMoisture = moistureSensorPin.getValue();
        logs.append(currentDateTime).append("  ").append("The moisture level is ").append(currentMoisture).append(" .\n");

        while (currentMoisture < MOISTURE_THRESHOLD) {

            oledDisplay.getCanvas().clear();
            oledDisplay.getCanvas().drawString(0, 0, currentDateTime);
            oledDisplay.getCanvas().drawString(0, 15, "Moisture level: " + currentMoisture);

            if (wateringSeconds == 0) {
                logs.append(getCurrentDateTime()).append("  ").append("The plant is thirsty. Watering for 2 seconds.\n");
                oledDisplay.getCanvas().drawString(0, 30, "Plant is thirsty.");
                oledDisplay.getCanvas().drawString(0, 45, "Watering for 2 sec.");
            } else {
                logs.append(getCurrentDateTime()).append("  ").append("The plant is still thirsty. Watering for 2 seconds.\n");
                oledDisplay.getCanvas().drawString(0, 30, "Plant is thirsty.");
                oledDisplay.getCanvas().drawString(0, 45, "Watering for 2 sec.");
            }
            oledDisplay.display();

            try {
                mosfetPin.setValue(1);
                Thread.sleep(2000);
                mosfetPin.setValue(0);

                wateringSeconds = wateringSeconds + 2;

                Thread.sleep(20000);

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            currentMoisture = moistureSensorPin.getValue();
        }

        oledDisplay.getCanvas().clear();
        oledDisplay.getCanvas().drawString(0, 0, currentDateTime);
        oledDisplay.getCanvas().drawString(0, 15, "Moisture level: " + currentMoisture);
        oledDisplay.getCanvas().drawString(0, 30, "Plant is fine.");
        oledDisplay.getCanvas().drawString(0, 45, "");
        oledDisplay.display();

        logs.append(getCurrentDateTime()).append("  ").append("The moisture level is now sufficient, after ").append(wateringSeconds).append(" seconds of watering.\n");
        System.out.println(logs);
    }

    private static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy h:mm a");
        return sdf.format(new Date());
    }
}
