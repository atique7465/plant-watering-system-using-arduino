import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

/**
 * @author atiQue
 * @since 18'Jul 2022 at 9:06 PM
 */

public class WateringTask extends TimerTask {

    private static final double MOISTURE_THRESHOLD = 2.60;

    private final Pin moisturePin;
    private final Pin motorPin;
    private final SSD1306 theOledObject;

    public WateringTask(Pin moisturePin, Pin motorPin, SSD1306 aDisplayObject) {
        this.moisturePin = moisturePin;
        this.motorPin = motorPin;
        this.theOledObject = aDisplayObject;
    }

    @Override
    public void run() {

        boolean needToWater = false;
        long moistureLevel = moisturePin.getValue();
        Date now = new Date();

        theOledObject.getCanvas().clear();
        theOledObject.getCanvas().drawString(0, 0, "Date: " + now);
        theOledObject.getCanvas().drawString(0, 20, "Moisture level of the plant is: " + moistureLevel);

        if (moistureLevel > MOISTURE_THRESHOLD) {
            System.out.println("The plant is thirsty.");
            theOledObject.getCanvas().drawString(0, 40, "The plant is thirsty. Watering the plant.");
            needToWater = true;
        } else {
            System.out.println("The plant is fine.");
            theOledObject.getCanvas().drawString(0, 40, "The plant is fine.");
        }

        theOledObject.display();

        if (needToWater) {
            try {
                System.out.println("Staring the motor for 1 second.");
                motorPin.setValue(1);
                Thread.sleep(1000);

                System.out.println("Turning off the motor.");
                motorPin.setValue(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
