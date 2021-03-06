/* Christopher Harris
 * Trystan Lea
 * 
 * 4/08/2014

 * This class:
 * - Starts up the communication with the Arduino via serial
 * - Reads the serial data coming in from the Arduino and print's it out to terminal
 * - The port must be specified in 'Program.java'

 * Code builds upon this great example:
 * http://www.csc.kth.se/utbildning/kth/kurser/DH2400/interak06/SerialWork.java
 */

package arduinocomm;

//Load Libraries
import java.io.*;
import java.util.TooManyListenersException;

//Load RXTX Library
import gnu.io.*;
import java.util.StringTokenizer;


/**
 * ArduinoComm :
 * Actively listens to open port and stored data in string buffer.
 * It is used (or at least its methods are) by both the main and myArduino threads.
 * @author christopher
 */
class ArduinoComm implements SerialPortEventListener {

    // Declare serial port Object
    SerialPort mySerialPort;
    
    // Declare input steam
    InputStream in;
    protected BufferedReader br;
    
    boolean stop = false;
    private final Object lockbr = new Object();

    /**
     * Method :: Start :
     * This opens the communication port with the Arduino.
     * Actively listens to the port for serial event.
     * Stores serial data into string buffer
     * @param portName
     * @param baudRate 
     */
    public void start(String portName, int baudRate) {
        stop = false;
        try {
            //Finds and opens the port
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier( portName );
            mySerialPort = (SerialPort) portId.open("my_java_serial" + portName, 2000);
            System.out.println("start :: Serial port found and opened");

            //configure the port
            try {
                mySerialPort.setSerialPortParams(baudRate,
                        mySerialPort.DATABITS_8,
                        mySerialPort.STOPBITS_1,
                        mySerialPort.PARITY_NONE);
                System.out.println("start :: Serial port params set: " + baudRate);
            } catch (UnsupportedCommOperationException e) {
                System.out.println("start :: Probably an unsupported Speed");
            }

            //establish stream for reading from the port
            try {
                in = mySerialPort.getInputStream();

                // Buffered reader allows us to read a line
                // Synchronize keeps threads from interfering
                // Thread.sleep(10);
                synchronized (lockbr) 
                {
                    br = new BufferedReader(new InputStreamReader(in));
                }
            } catch (IOException e) {
                System.out.println("start :: couldn't get streams");
            }

            // we could read from "in" in a separate thread, but the API gives us events
            try {
                mySerialPort.addEventListener(this);
                mySerialPort.notifyOnDataAvailable(true);
                System.out.println("start :: Event listener added");
            } catch (TooManyListenersException e) {
                System.out.println("start :: Couldn't add listener");
            }
        } catch (Exception e) {
            System.out.println("start :: Port in Use: " + e);
        }
    } // End of start

    /**
     * Method :: closeSerialPort :
     * closes serial connection
    */ 
    public void closeSerialPort() {
        try {
            in.close();
            stop = true;
            mySerialPort.close();
            System.out.println("closeSerialPort :: Serial port closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    } // End closeSerialPort

    @Override
    public void serialEvent(SerialPortEvent event) {
        int x = 4;
        int[] valArr = new int[x];

        //Reads in data while data is available
        while (event.getEventType() == SerialPortEvent.DATA_AVAILABLE && stop == false) {
            
            // A check
            synchronized (lockbr) {
                try {
                    random = br.readLine();
                    //System.out.println(br.readLine());
                } catch (IOException ex) {
                }
            }
        } // End Event while loop
    } // End serialEvent

    // Takes the String buffer, returns int array
    public synchronized int[] InputTokenizer() {
        int arrCount = 0;
        int x = 4;
        int[] valArr = new int[x];

        // Keep threads from interfering with the buffer string
        synchronized (lockbr)
        {
            StringTokenizer str = new StringTokenizer(random);
            while (str.hasMoreTokens()) {
                valArr[arrCount] = Integer.parseInt(str.nextToken());
                arrCount++;
            }
        }
        return valArr;
    } // End InputTokenizer
    String random = "100 100 100 100";

} // End ArduinoComm