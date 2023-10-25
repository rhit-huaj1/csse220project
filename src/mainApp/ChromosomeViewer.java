package mainApp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

public class ChromosomeViewer {
	/**
	 * ensures: creates, initializes, and sets visible the Viewer's frame and
	 * component
	 */

	public String fileName = "Chromosome X";
	public String filePath = "";
	public File file;
	public JFrame frame;
	public ChromosomeComponent chComponent;
	
	public static final int BORDER = 20;
	
	public int getGeneWidth() {return (frame.getWidth()-BORDER*2)/(chComponent.getChromosome().getNumPerRow());}

	public void driverMain() {
		final String frameTitle = "Chromosome Viewer";
		final int frameWidth = 310;
		final int frameHeight = 420;
		final int textFieldWidth = 5; // TODO check value

		this.frame = new JFrame();
		frame.setTitle(frameTitle);
		frame.setSize(frameWidth, frameHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(frameWidth, frameHeight));
		//frame.setResizable(false);
		
		frame.setVisible(true);
		
		// public int getFrameWidth() {return frame.getWidth()};

		// fileName - BorderLayout.NORTH
		JLabel fileNameLabel = new JLabel(fileName);
		frame.add(fileNameLabel, BorderLayout.NORTH);

		// chromosome - BorderLayout.CENTER
		this.chComponent = new ChromosomeComponent();
		frame.add(chComponent);

		// buttons/fields - BorderLayout.SOUTH
		JLabel mRate = new JLabel("M Rate:");
		JTextField mRateField = new JTextField("1", textFieldWidth);

		/**
		 * Functional Mutate Button
		 */
		JButton mutateButton = new JButton("Mutate");
		mutateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Get the mutation rate as a percentage from the text field
				int mutationRate = Integer.parseInt(mRateField.getText());

				if (mutationRate < 0 || mutationRate > 100) {
					// Handle invalid input, show an error message, etc.
					JOptionPane.showMessageDialog(frame,
							"Invalid mutation rate. Please enter a number between 0 and 100.", "Invalid Input",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Perform mutation for each gene based on the mutation rate
				for (Gene gene : chComponent.getChromosome().genes) {
					// Generate a random number between 1 and 100
					int randomNum = (int) (Math.random() * 100) + 1;
					if (randomNum <= mutationRate) {
						// Mutate the gene
						gene.changeBit();
					}
				}
				// Repaint the frame to reflect the changes
				frame.repaint();
			}

		});

		// Load button functionality
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooseFile = new JFileChooser();
//				chooseFile.setCurrentDirectory(new File("C:\\Users\\%USERNAME%\\Documents\\GARP\\"));
				int response = chooseFile.showOpenDialog(null);

				if (response == JFileChooser.APPROVE_OPTION) {
					file = new File(chooseFile.getSelectedFile().getAbsolutePath());
					// Storing file name
					// TODO check which one to use
					fileName = file.getName();
					filePath = file.getPath();
					fileNameLabel.setText(fileName);
					// test
//					System.out.println(file);
//					System.out.println(fileName);
					
					try {
						List<String> lines = Files.readAllLines(file.toPath());
						
						for (String s : lines) {
//							if (s.length() != 100) {
//		                        JOptionPane.showMessageDialog(frame,
//		                                "The loaded file does not have the expected format (100 characters per line).",
//		                                "File Format Error",
//		                                JOptionPane.ERROR_MESSAGE);
//		                        return;
//		                    }
							
							chComponent.setChromosome(new Chromosome());
							chComponent.handleStoreChromosomeData(s);
							chComponent.handleInitiateGeneWithFile();
							frame.repaint();
						}
					} catch (IOException e1) {
						// Display an error message if there's an issue with the file
		                JOptionPane.showMessageDialog(frame,
		                        "An error occurred while loading the file.",
		                        "File Load Error",
		                        JOptionPane.ERROR_MESSAGE);
		                System.out.println("Illegal file type");
		                e1.printStackTrace();
					}
				}
			}
		});

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        fileChooser.setCurrentDirectory(new File("C:\\Users\\%USERNAME%\\Documents\\GARP"));
		        int response = fileChooser.showSaveDialog(null);

		        if (response == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
		            if (!file.getName().toLowerCase().endsWith(".txt")) {
		                file = new File(file.getAbsolutePath() + ".txt");
		            }
	
		            // Get the chromosome data in the required format (1 for black, 0 for green)
		            String chromosomeData = chComponent.getChromosome().getChromosomeDataAsString();
	
		            try (FileWriter writer = new FileWriter(file)) {
		                writer.write(chromosomeData);
		            } catch (IOException ex) {
		                ex.printStackTrace();
		            }
		        }
				
//				try {
//					PrintWriter writer = new PrintWriter(filePath);
//					BufferedWriter bWriter = new BufferedWriter(writer);
//					for (Gene gene : chComponent.getChromosome().genes) {
//						bWriter.write(gene.getBit());
//					}
//					bWriter.close();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
			}
		});

		/**
		 * panel.add(mutateButton, BorderLayout.EAST); panel.add(mRate,
		 * BorderLayout.EAST); panel.add(mRateField, BorderLayout.CENTER);
		 * panel.add(loadButton, BorderLayout.CENTER); panel.add(saveButton,
		 * BorderLayout.CENTER);
		 */
		
		JPanel buttonPanel = new JPanel();
		frame.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(mRate);
		buttonPanel.add(mRateField);
		buttonPanel.add(mutateButton);
		buttonPanel.add(loadButton);
		buttonPanel.add(saveButton);

//		JPanel panel = new JPanel(new GridBagLayout());
//		GridBagConstraints c = new GridBagConstraints();
//		frame.add(panel, BorderLayout.SOUTH);
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.ipady = GridBagConstraints.VERTICAL;
//		c.gridx = 0;
//		c.gridy = 0;
//		panel.add(mutateButton, c);
//		c.gridx = 1;
//		c.gridy = 0;
//		panel.add(mRate, c);
//		c.gridx = 2;
//		c.gridy = 0;
//		panel.add(mRateField);
//		c.gridx = 0;
//		c.gridy = 1;
//		panel.add(loadButton, c);
//		c.gridx = 1;
//		c.gridy = 1;
//		panel.add(saveButton, c);

		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO figure out how this will change it on click
				Gene gene = chComponent.containsGene(e.getX(), e.getY());
				if (gene != null) {
					gene.changeBit();
				}
				fileNameLabel.setText(fileName+" (new)"); // Appends the label text to include (new) if edited
				frame.repaint();
			}
		});

		frame.pack();
	} // driverMain

	public void handleDriverMain() {
		this.driverMain();
	}

	public static void main(String[] args) {
		ChromosomeViewer c = new ChromosomeViewer();
		c.handleDriverMain();
	} // main
}
