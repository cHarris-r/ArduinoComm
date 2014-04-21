package arduinocomm;

// ArduinoComm software part of OpenEnergyMonitor.org project

/*
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
// Author: Trystan Lea

//Main program class
public class Program {

    // Declares the Arduino object field
    // Declares myArduino thread
    public static ArduinoComm myArduino;
    // public static ArduinoComm myArduino = new ArduinoComm();

    // Main method
    public static void main(String args[]) {
        // Assigns myArduino thread
        myArduino = new ArduinoComm();

        // Start the arduino connection - usb port - baud rate
        myArduino.start("/dev/rfcomm0", 9600); // rfcomm0, 9600

        //TODO Pause here while thread is started
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
            int[] boardOutput = myArduino.InputTokenizer();
            System.out.println("Cycle:");
            for (int i = 0; i < boardOutput.length; i++) {
                System.out.println("valArr[" + i + "]" + boardOutput[i]);
            }
            System.out.println();
            System.out.flush();
            String outputChar = asltech.GestureRecognition.doRecognition( boardOutput );
            
            //asltech.GestureRecognition.sayShit("hello");
            asltech.GestureRecognition.sayShit( outputChar );
            
        }
    }
}