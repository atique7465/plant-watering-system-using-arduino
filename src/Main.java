import org.firmata4j.I2CDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;

import java.io.IOException;
import java.util.Timer;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        //initialize the grove board
        String myPort = "COM3";
        IODevice myGroveBoard = new FirmataDevice(myPort);
        myGroveBoard.start();
        myGroveBoard.ensureInitializationIsDone();

        // pin 14 to get input from potentiometer
        var moisturePin = myGroveBoard.getPin(Pins.A0);
        moisturePin.setMode(Pin.Mode.INPUT);

        // pin 6 to get input from button
        var motorPin = myGroveBoard.getPin(Pins.D2);
        motorPin.setMode(Pin.Mode.OUTPUT);

        // Set up the OLED display(type, size ...)
        I2CDevice i2cObject = myGroveBoard.getI2CDevice(Pins.I2CO);
        SSD1306 myOledDisplay = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        myOledDisplay.init();

        var task = new WateringTask(moisturePin, motorPin, myOledDisplay);

        new Timer().schedule(task, 0, 3600000);

        myGroveBoard.stop(); // finish with the board.
    }
}