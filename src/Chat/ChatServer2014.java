import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class ChatServer2014 extends JFrame 
{
    // Text area for displaying contents
    private JTextArea jta = new JTextArea();

    //Mapping of sockets to output streams - create a 
    //Hashtable here  
    private Hashtable hashtable = new Hashtable();

    //Just declare a Server socket here
    private ServerSocket serverSocket;


    public static void main(String[] args) 
    {
        //Create(instantiate) a ChatServer2014 here - no need to assign 
        //to a variable
        new ChatServer2014();
    }


    //The ChatServer constructor is complete
    public ChatServer2014() 
    {
        // Place text area on the frame
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);
        setTitle("MultiThreadServer");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setVisible(true); // It is necessary to show the frame here!
        jta.setEditable(false); // Disable editing of server log
    
        // Listen for connections
        listen();
    }




    //Method listen will listen for connections
    private void listen() 
    {
        //Do your server side coding here as in our examples and homework.  
        //Don't forget to add your DataOutputStream to your hashtable. 
        try 
        {
            // Create a server socket
            serverSocket = new ServerSocket(8000);
            jta.append("MultiThreadServer started at " + new Date() + '\n');
            
            while (true) 
            {
                // Listen for a connection request
                Socket socket = serverSocket.accept();
                
                // Display the client number
                jta.append("Connection from " + socket + " at " + new Date() + '\n');

                // Create output stream
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

                // Save output stream to hashtable
                hashtable.put(socket, dout);

                //Create a ServerThread to get input from the client
                //Note - you need to pass a reference to your ChatServer2014
                //and a reference to the socket	 
                new ServerThread(this, socket);
            }
        }
        catch(IOException ex) 
        {
            System.err.println(ex);
        }
         
    }
		
    // Create a method called "getOutputStreams" that will return an Enumeration or Hashtable
    // it will be used to get the output streams - just create and return an Enumeration
    // or an Iterator based on your Hashmap or Hashtable
    Enumeration getOutputStreams()
    {
        return hashtable.elements();
    }
  

    // Create a method called "sendToAll" that will be used to send message to all clients
    void sendToAll(String message)
    {
        
        //Go through hashtable while there are elements remaining in the Hashtable and send a message to each output stream
	//Get the next element and cast it to a DataOutputStream.  Use the writeUTF method of the DataOutputStream class
        //to write the message 
	// Go through hashtable and send message to each output stream
        for (Enumeration e = getOutputStreams(); e.hasMoreElements();)
        {
            DataOutputStream dout = (DataOutputStream)e.nextElement();
            try 
            {
                // Write message
                dout.writeUTF(message);
            } 
            catch (IOException ex) 
            {
                System.err.println(ex);
            }
        }  
    }

    //Inner class that defines the thread
    class ServerThread extends Thread   
    {
        private ChatServer2014 server;

        private Socket socket;

        /** Construct a thread */
        public ServerThread(ChatServer2014 server, Socket socket) 
        {
            //initialize the instance variables and start the thread
            this.server = server;
            this.socket = socket;
            start();
        
        }

        /** Run a thread */
        @Override
        public void run() 
        {
            //create the DataInputStream and continuously read from the client and
            //send text back to all clients.  Also append a the message to the JTextArea on the server	
            //catch IOExceptions
            try 
            {
                // Create data input and output streams
                DataInputStream din = new DataInputStream(socket.getInputStream());

                // Continuously serve the client
                while (true) 
                {
                    String string = din.readUTF();

                    // Send text back to the clients
                    server.sendToAll(string);

                    // Add chat to the server jta
                    jta.append(string + '\n');
                }
            }
            catch(IOException e) 
            {
                System.err.println(e);
            }
	}
    }
}
