/**
* Presentation Controller Module 
*
* @author  ADEM F. IDRIZ
* @version 1.0
* @since  2016 - TU Delft
*/

package presentationController;

import pal.TECS.*;


import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;


public class PresentationController extends JFrame {

	private PALClient thePalClient;
	private JTextArea textWindow= new JTextArea();
	private int slideNumber = 1;
	private int prevNumber= slideNumber;
	private String PPTcommand = "";
	private int counter = 0;
	private String command = "";
	
    static private final String newline = "\n";
 
    JTextField path = new JTextField();
    Desktop desktop = Desktop.getDesktop();
    File openedFile;
	
    // Constructor 
	public PresentationController(String clientName) {
		
        createAndShowGUI();
		
		setSize(600, 140);
	    setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		/*Initiate textWindow */
		setLayout(new BorderLayout());
		setSize(600, 500);
		
		//Put the drawing area in a scroll pane.
        JScrollPane scroller = new JScrollPane(textWindow);
        scroller.setPreferredSize(new Dimension(600,360));
        add(scroller, BorderLayout.SOUTH);
        
		
	    setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setTitle(clientName);
		
		/*Create Connection and Subscription */
		connectClientandSubscribe(clientName);
		
	}
	
	  private void connectClientandSubscribe(final String clientName) {

		/*Manually assigned IP */
		String tecsserver = "127.0.0.1";
		//String tecsserver = System.getProperty(TECSSERVER_IP_PROP);
		thePalClient = new PALClient(tecsserver, clientName);
		    
		/*Subscription */
		thePalClient.subscribe(palConstants.SlideControlMsg, palEventHandler_SlideControl);
		
		
		// Start Listening slideNumbers 
		thePalClient.startListening();
	  }
	
	 // Handle type of SlideControlMsg 
	  EventHandler<SlideControlCommand> palEventHandler_SlideControl = new EventHandler<SlideControlCommand>() {
		    public void handleEvent(SlideControlCommand event) {
		    	counter++;
	
		    	// Parsing Messages
		    	slideNumber = ((SlideControlCommand) event).SlideNumber;
		    	
		    	if (slideNumber>prevNumber){
		    		
		        	try {
		        		pressKey(KeyEvent.VK_RIGHT);
		        		System.out.println("Next Slide");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
		    	}
	
		        prevNumber= slideNumber;
		    	
		    	PPTcommand = ((SlideControlCommand) event).command;
		    	
		        int intIndex = PPTcommand.indexOf("&");
		        
		        if(intIndex == - 1){

		           command = PPTcommand;
 
		        }else{

					String[] partsPPTcommand = PPTcommand.split("&");
					
					command = partsPPTcommand [0];
					

		        }
		        
		    	System.out.println("Slide Number: "+ slideNumber + "  &  " + "PPTcommand: " + command);
				textWindow.append(counter+ "- Slide Number: " + slideNumber + "  &  " + "PPTcommand: " + command +  System.getProperty("line.separator"));
				textWindow.append("------------------------------------------------------------------------------------------------------------------------------------------------" + System.getProperty("line.separator")+ System.getProperty("line.separator"));

	        
		        // Cases for Command Messages
		        if (command.equals("START")) {
			        // F5
		        	try {
						pressKey(KeyEvent.VK_F5);
						System.out.println("START");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	
		        }else if (command.equals("END")){
			        // Esc
		        	try {

						pressKey(KeyEvent.VK_ESCAPE);
						System.out.println("END");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	
		        }else if (command.equals("NEXT")){
			        // RIGHT ARROW
		        	try {

						pressKey(KeyEvent.VK_RIGHT);
						System.out.println("Next Command");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	
		        }else if (command.equals("PREVIOUS")){
			        // LEFT ARROW

		        	try {

						pressKey(KeyEvent.VK_LEFT);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	
		        }else if (command.equals("HOME")){
			        // Home
			        	try {
	
							pressKey(KeyEvent.VK_HOME);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	
		        }else if (command.equals("GOTO")){
		            // <number>+ENTER
		        	
		        }else{
		        	// No Command
		        	
		        }
		        
				
		    }
		  };

	  // Execute PPT Command
		public void pressKey(int keyvalue) throws IOException{
        	try {
        		// Make sure file opened and focused 
        		desktop.open(openedFile);
        		
        		// Press Keys virtually
        		Robot robotdefault = new Robot();
        		// robotdefault.delay(2000);
        		// robotdefault.delay(100);
        		robotdefault.delay(50);
				robotdefault.keyPress(keyvalue);
				robotdefault.keyRelease(keyvalue);

			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
		}
		  
			
  
		// Create GUI
		private void createAndShowGUI() {
		
		//Turn off metal's use of bold fonts
        UIManager.put("swing.boldMetal", Boolean.FALSE); 
			
		// Panel
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		// File Path
		panel.add(path);
	    path.setBounds(140, 10, 430, 30);

        // OPEN FILE Button
	    JButton openButton = new JButton("OPEN FILE...");
		openButton.setBounds(10, 10, 120, 30);
		panel.add(openButton);
        openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				//first check if Desktop is supported by Platform or not
		        if(!Desktop.isDesktopSupported()){
		            System.out.println("Desktop is not supported");
		            return;
		        }
		         
		        //Create a file chooser
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setCurrentDirectory(new File("/Users/adem/Desktop"));
				
				int result = jFileChooser.showOpenDialog(new JFrame());
			
			
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = jFileChooser.getSelectedFile();
				    
				    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
				    if(selectedFile.exists())
						try {
							// Open selected file
							desktop.open(selectedFile);
							path.setText(selectedFile.getAbsolutePath());
							openedFile=selectedFile;
							textWindow.append("Opened File: " + selectedFile.getAbsolutePath()+ newline);
							textWindow.append("*******************************************************************************************************************" + newline + newline);

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}

			}

			
		});

        
		// Start Button
		JButton startButton = new JButton("START");
		startButton.setBounds(10, 50, 100, 30);
		panel.add(startButton);
		
		
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (openedFile.exists()) 
			        // F5
		        	try {
						pressKey(KeyEvent.VK_F5);
						System.out.println("START");
					} catch (IOException eS) {
						// TODO Auto-generated catch block
						eS.printStackTrace();
					}
				
			}
		});

		
		// Next Button
		JButton nextButton = new JButton("NEXT");
		nextButton.setBounds(120, 50, 100, 30);
		panel.add(nextButton);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (openedFile.exists()) 
		        // RIGHT ARROW
	        	try {

					pressKey(KeyEvent.VK_RIGHT);
					System.out.println("NEXT");
				} catch (IOException eR) {
					// TODO Auto-generated catch block
					eR.printStackTrace();
				}

			}
		});
		
		// Previous Button
		JButton previousButton = new JButton("PREVIOUS");
		previousButton.setBounds(230, 50, 100, 30);
		panel.add(previousButton);
		previousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (openedFile.exists()) 
				// LEFT ARROW
				try {
					pressKey(KeyEvent.VK_LEFT);
					System.out.println("PREVIOUS");
				} catch (IOException eL) {
					// TODO Auto-generated catch block
					eL.printStackTrace();
				}

			}
		});
		
		
		// HOME Button
		JButton homeButton = new JButton("HOME");
		homeButton.setBounds(340, 50, 100, 30);
		panel.add(homeButton);
		homeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (openedFile.exists()) 
		        // Home
	        	try {

					pressKey(KeyEvent.VK_HOME);
					System.out.println("HOME");
				} catch (IOException eH) {
					// TODO Auto-generated catch block
					eH.printStackTrace();
				}

			}
		});

		// END Button
		JButton endButton = new JButton("END");
		endButton.setBounds(450, 50, 100, 30);
		panel.add(endButton);
		endButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (openedFile.exists()) 
		        // ESC
	        	try {

					pressKey(KeyEvent.VK_ESCAPE);
					System.out.println("END");
				} catch (IOException eH) {
					// TODO Auto-generated catch block
					eH.printStackTrace();
				}

			}
		});

	
		}
		
		// MAIN
	    public static void main(String[] args) {
	    	
	    	//creating and showing this application's GUI.
	    	new PresentationController("PresentationController");
    	}

}

