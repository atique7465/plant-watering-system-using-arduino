import org.firmata4j.I2CDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;

import java.io.IOException;
import java.util.Timer;

public class Main {

    private static final long ONE_HOUR_IN_MILLIS = 3600000;

    public static void main(String[] args) throws IOException, InterruptedException {

        String myPort = "COM3";
        IODevice myGroveBoard = new FirmataDevice(myPort);
        myGroveBoard.start();
        myGroveBoard.ensureInitializationIsDone();
        System.out.println("Arduino Board Started for Watering Process.");

        // Moisture Sensor
        var moistureSensorPin = myGroveBoard.getPin(Pins.A2);

        // Mosfet
        var mosfetPin = myGroveBoard.getPin(Pins.D7);

        // Set up the OLED display(type, size ...)
        I2CDevice i2cObject = myGroveBoard.getI2CDevice(Pins.I2CO);
        SSD1306 myOledDisplay = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        myOledDisplay.init();

        var task = new WateringTask(moistureSensorPin, mosfetPin, myOledDisplay);

        new Timer().schedule(task, 0, ONE_HOUR_IN_MILLIS);
    }
}