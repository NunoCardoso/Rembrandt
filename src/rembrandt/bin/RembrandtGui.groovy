/** This file is part of REMBRANDT - Named Entity Recognition Software
 *  (http://xldb.di.fc.ul.pt/Rembrandt)
 *  Copyright (c) 2008-2009, Nuno Cardoso, University of Lisboa and Linguateca.
 *
 *  REMBRANDT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  REMBRANDT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with REMBRANDT. If not, see <http://www.gnu.org/licenses/>.
 */
 
package rembrandt.bin

import rembrandt.gui.*
import saskia.bin.Configuration
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import org.apache.log4j.*
import org.apache.commons.cli.*


 /**
  * @author Nuno Cardoso
  * 
  * This is the GUI for all Rembrandt processes.
  */
  class RembrandtGui {

	 Configuration conf
	 Rembrandt rembrandt
	 static Logger log = Logger.getLogger("RembrandtGui")
	
       // Initialize all swing objects.
    private JFrame frame = new JFrame("REMBRANDT"); //create Frame
    private JPanel pnlNorth = new JPanel(); // North quadrant 
    private JPanel pnlSouth = new JPanel(); // South quadrant
    private JPanel pnlEast = new JPanel(); // East quadrant
    private JPanel pnlWest = new JPanel(); // West quadrant
    private JPanel pnlCenter = new JPanel(); // Center quadrant

	 private JPanel pnlNorth1 = new JPanel(); 
	 private JPanel pnlNorth2 = new JPanel(); 
	
	 private JPanel pnlNorth3 = new JPanel(); 
	 private JPanel pnlNorth4 = new JPanel(); 
	 private JPanel pnlNorth5 = new JPanel(); 
	 
	 private JLabel labelStatus = new JLabel ("Ready.");
	 private JLabel labelRules = new JLabel ("Rules");
	 private JLabel labelLanguage = new JLabel ("Language");
	 private JLabel labelStream = new JLabel ("Stream");
	 private JLabel labelInput = new JLabel ("Input");
	 private JLabel labelOutput = new JLabel ("Output");
	 private JLabel labelErrput = new JLabel ("Output");
	 private JLabel labelEncoding = new JLabel ("Encoding");
	 private JLabel labelReaderWriter = new JLabel ("Reader/Writer");
	 private JLabel labelStyleTag = new JLabel ("StyleTag");
	 private JLabel labelTagVerbosity = new JLabel ("Tag Verbosity");

	 private JComboBox comboBoxRules = new JComboBox (new Vector(["HAREM"]));
	 private JComboBox comboBoxLanguage = new JComboBox (new Vector(["pt","en"]));

	 private JComboBox comboBoxInputStyleVerbose = new JComboBox (new Vector(["0","1","2","3"]));
	 private JComboBox comboBoxOutputStyleVerbose = new JComboBox (new Vector(["0","1","2","3"]));
	 private JComboBox comboBoxErrputStyleVerbose = new JComboBox (new Vector(["0","1","2","3"]));

	 private JComboBox comboBoxInputEncoding = new JComboBox (new Vector(["ISO-8859-1","MacRoman","UTF-8"]));
	 private JComboBox comboBoxOutputEncoding = new JComboBox (new Vector(["ISO-8859-1","MacRoman","UTF-8"]));
	 private JComboBox comboBoxErrputEncoding = new JComboBox (new Vector(["ISO-8859-1","MacRoman","UTF-8"]));

	 private JComboBox comboBoxInputReader = new JComboBox ();
	 private JComboBox comboBoxOutputWriter = new JComboBox ();
	 private JComboBox comboBoxErrputWriter = new JComboBox ();

	 private JComboBox comboBoxInputStyleTag = new JComboBox ();
	 private JComboBox comboBoxOutputStyleTag = new JComboBox ();
	 private JComboBox comboBoxErrputStyleTag = new JComboBox ();

	 private JTextArea textAreaInput = new JTextArea();
	 private JTextArea textAreaOutput = new JTextArea();

    // Buttons
    private JButton buttonAnnotate = new JButton("Annotate!");
    
    // Menu
    private JMenuBar menuBar = new JMenuBar(); // Menubar
    private JMenu menuFile = new JMenu("File"); // File Entry on Menu bar
    private JMenuItem menuOpen = new JMenuItem("Open..."); // Quit sub item
    private JMenuItem menuSave = new JMenuItem("Save..."); // Quit sub item
    private JMenuItem menuImport = new JMenuItem("Import..."); // Quit sub item
    private JMenuItem menuExport = new JMenuItem("Export..."); // Quit sub item
    private JMenuItem menuQuit = new JMenuItem("Quit"); // Quit sub item
    private JMenu menuHelp = new JMenu("Help"); // Help Menu entry
    private JMenuItem menuConfiguration = new JMenuItem("Configuration"); // About Entry
    private JMenuItem menuAbout = new JMenuItem("About"); // About Entry

	// others
	File inputfile, outputfile

	 public RembrandtGui(Rembrandt rembrandt, Configuration conf) {
		 this.rembrandt = rembrandt
		 this.conf = conf
			
	     frame.setJMenuBar(menuBar);
        
        //Build Menus
        menuFile.add(menuOpen);  // Create Quit line
        menuFile.add(menuSave);  // Create Quit line
        menuFile.add(menuImport);  // Create Quit line
        menuFile.add(menuExport);  // Create Quit line
        menuFile.add(menuQuit);  // Create Quit line
        menuHelp.add(menuConfiguration); // Create About line
        menuHelp.add(menuAbout); // Create About line
        menuBar.add(menuFile);        // Add Menu items to form
        menuBar.add(menuHelp);

		  // add stuff to North2 
		
		  pnlNorth2.setLayout(new GridLayout(0,5));
		  // add stuff to North
		  pnlNorth2.add(labelStream);
		  pnlNorth2.add(labelEncoding);
		  pnlNorth2.add(labelReaderWriter);
		  pnlNorth2.add(labelStyleTag);
		  pnlNorth2.add(labelTagVerbosity);
		
		  pnlNorth2.add(labelInput);
		  comboBoxInputEncoding.setEditable(true);
		  comboBoxInputEncoding.setSelectedItem(rembrandt.inputEncodingParam);
		  pnlNorth2.add(comboBoxInputEncoding);
		
		  comboBoxInputReader.setEditable(true);
		  def cl = ClassFilter.getReaderClasses()
		  println cl
		  comboBoxInputReader.setSelectedItem(rembrandt.inputReaderParam);
		  pnlNorth2.add(comboBoxInputReader);
		  pnlNorth2.add(comboBoxInputStyleTag);
		  pnlNorth2.add(comboBoxInputStyleVerbose);

		  pnlNorth2.add(labelOutput);
		  comboBoxOutputEncoding.setEditable(true);
		  comboBoxOutputEncoding.setSelectedItem(rembrandt.outputEncodingParam);
		  pnlNorth2.add(comboBoxOutputEncoding);

		  comboBoxOutputWriter.setEditable(true);
		  comboBoxOutputWriter.setSelectedItem(rembrandt.outputWriterParam);
		  pnlNorth2.add(comboBoxOutputWriter);
		  pnlNorth2.add(comboBoxOutputStyleTag);
		  pnlNorth2.add(comboBoxOutputStyleVerbose);

		  pnlNorth2.add(labelErrput);
		  comboBoxErrputEncoding.setEditable(true);
		  comboBoxErrputEncoding.setSelectedItem(rembrandt.errEncodingParam);
		  pnlNorth2.add(comboBoxErrputEncoding);

		  comboBoxErrputWriter.setEditable(true);
		  comboBoxErrputWriter.setSelectedItem(rembrandt.errWriterParam);
		  pnlNorth2.add(comboBoxErrputWriter);
		  pnlNorth2.add(comboBoxErrputStyleTag);
		  pnlNorth2.add(comboBoxErrputStyleVerbose);
		
		  // add stuff to North
		  pnlNorth1.setLayout(new FlowLayout());
		  pnlNorth1.add(labelLanguage);
		  pnlNorth1.add(comboBoxLanguage);
		  pnlNorth1.add(labelRules);
		  pnlNorth1.add(comboBoxRules);
		  pnlNorth1.add(buttonAnnotate);
		
		  pnlNorth.add(pnlNorth1);
		  pnlNorth.add(pnlNorth2);
       


/*	patternList.setEditable(true);
	 petList.setSelectedIndex(4);
	 petList.addActionListener(this);
*/	
	
		// add stuff to West 
        pnlWest.add(textAreaInput);

		// add stuff to East
		  pnlEast.add(textAreaOutput);

		// add stuff to South
		  pnlSouth.add(labelStatus);
  
        // Setup Main Frame
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(pnlNorth, BorderLayout.NORTH);
        frame.getContentPane().add(pnlSouth, BorderLayout.SOUTH);
        frame.getContentPane().add(pnlEast, BorderLayout.EAST);
        frame.getContentPane().add(pnlWest, BorderLayout.WEST);
        //f.getContentPane().add(pnlCenter, BorderLayout.CENTER);
        

        // Allows the Swing App to be closed
        frame.addWindowListener(new ListenCloseWdw());
                
        //Add Menu listener
        menuOpen.addActionListener(new ListenMenuOpen());
        menuQuit.addActionListener(new ListenMenuQuit());
    }

    public class ListenMenuOpen implements ActionListener{
        public void actionPerformed(ActionEvent e){
				JFileChooser fileChooser = new JFileChooser()
            int retval = fileChooser.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                //... The user selected a file, get it, use it.
                this.inputfile = fileChooser.getSelectedFile();

                
            }      
        }
    }

	public class ListenMenuSave implements ActionListener{
        public void actionPerformed(ActionEvent e){
				JFileChooser fileChooser = new JFileChooser()
            int retval = fileChooser.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                //... The user selected a file, get it, use it.
                this.outputfile = fileChooser.getSelectedFile();

                
            }      
        }
    }
       
    public class ListenMenuQuit implements ActionListener{
        public void actionPerformed(ActionEvent e){
            System.exit(0);         
        }
    }

	  public class ListenCloseWdw extends WindowAdapter{
        public void windowClosing(WindowEvent e){
            System.exit(0);         
        }
    }
	
	  public start() {
        // Display Frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); //Adjusts panel to components for display
        frame.setVisible(true);
		}
	
	 public main() {
	 	def conf, conffilepath
		Options o = new Options()
		o.addOption("conf", true, "Configuration file")
		o.addOption("help", false, "Gives this help information")
	    
		CommandLineParser parser = new GnuParser()
		CommandLine cmd = parser.parse(o, args)

		if (cmd.hasOption("help")) {
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp( "java rembrandt.bin.RembrandtGui", o )
	    System.exit(0)
		}
		if (!cmd.hasOption("conf")) {
	    conffilepath = Configuration.defaultconf
	    log.info "No configuration file given. Using default configuration file."
 		} else {
 	    conffilepath = cmd.getOptionValue("conf")
	    log.info "Configuration file $conffilepath given."
 		}
		conf = Configuration.newInstance(conffilepath) 
		Rembrandt rembrandt = new Rembrandt(conf)
		log.info "Rembrandt version ${Rembrandt.getVersion()}. Welcome."
		RembrandtGui gui = new RembrandtGui(rembrandt, conf)
		gui.start()
	}
}