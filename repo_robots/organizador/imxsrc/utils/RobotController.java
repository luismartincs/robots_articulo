package utils;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * This example code demonstrates how to perform simple state
 * control of a GPIO pin on the Raspberry Pi.
 *
 * @author Robert Savage
 */
public class RobotController {

   private GpioController gpio;
   private GpioPinDigitalOutput llantaI1;
   private GpioPinDigitalOutput llantaI2;
   private GpioPinDigitalOutput llantaD1;
   private GpioPinDigitalOutput llantaD2;


   public RobotController(){
	config();
   }

   private void config(){
    	System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        // create gpio controller
        gpio = GpioFactory.getInstance();

        // provision gpio pin #01 as an output pin and turn on
        llantaI1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "L1", PinState.LOW);
        llantaI2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "L2", PinState.LOW);
        llantaD1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, "R1", PinState.LOW);
        llantaD2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09, "R2", PinState.LOW);


        // set shutdown state for this pin
        llantaI1.setShutdownOptions(true, PinState.LOW);
        llantaI2.setShutdownOptions(true, PinState.LOW);
        llantaD1.setShutdownOptions(true, PinState.LOW);
        llantaD2.setShutdownOptions(true, PinState.LOW);
   }

   public void doAction(char action){
	try{
	switch(action){

		case 'F':
			avanzar();
			break;	
		case 'B':
			retroceder();
			break;
		default:break;

	}

	}catch(Exception ex){
		System.out.println(ex.getMessage());
	}

   }

   public void avanzar() throws InterruptedException{
	llantaI1.high();
	llantaI2.low();
	llantaD1.high();
	llantaD2.low();
	Thread.sleep(1000);
   	gpio.shutdown();
   }

   public void retroceder() throws InterruptedException{
	llantaI1.low();
	llantaI2.high();
	llantaD1.low();
	llantaD2.high();
	Thread.sleep(1000);
   	gpio.shutdown();
   }

}