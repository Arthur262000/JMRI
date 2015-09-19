//AlternateTrackAction.java

package jmri.jmrit.operations.locations;

import java.awt.GridBagLayout;
import java.awt.event.*;

import javax.swing.*;

import java.util.ResourceBundle;

import jmri.jmrit.operations.OperationsFrame;
import jmri.jmrit.operations.OperationsXml;


/**
 * Action to launch selection of alternate track.
 * @author Daniel Boudreau Copyright (C) 2011
 * @version     $Revision: 17977 $
 */
public class AlternateTrackAction extends AbstractAction {
		
	static final ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrit.operations.locations.JmritOperationsLocationsBundle");
	
	private TrackEditFrame _tef;
	
	public AlternateTrackAction(TrackEditFrame tef){
		super(rb.getString("AlternateTrack"));
		_tef = tef;
	}
	
	 public void actionPerformed(ActionEvent e) {
		new AlternateTrackFrame(_tef);
	 }
	
}

class AlternateTrackFrame extends OperationsFrame{
	
	static final ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrit.operations.locations.JmritOperationsLocationsBundle");
	
	// combo boxes
	JComboBox trackBox = new JComboBox();
	
	// radio buttons
	
    // major buttons
    JButton saveButton = new JButton(rb.getString("Save"));
    
    Track _track;
	
	public AlternateTrackFrame(TrackEditFrame tef){
		super();
		
		// the following code sets the frame's initial state
	    getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
	    
	    _track = tef._track;
		
		// load the panel
		
		// row 2
		JPanel pAlternate = new JPanel();
		pAlternate.setLayout(new GridBagLayout());
		pAlternate.setBorder(BorderFactory.createTitledBorder(""));
		addItem(pAlternate, trackBox, 0, 0);

		_track.getLocation().updateComboBox(trackBox);
		trackBox.removeItem(_track);	// remove this track from consideration
		trackBox.setSelectedItem(_track.getAlternativeTrack());

		JPanel pControls = new JPanel();
		pControls.add(saveButton);
  	
    	// button action
    	addButtonAction(saveButton);
    	
    	getContentPane().add(pAlternate);
    	getContentPane().add(pControls);
    	
    	setTitle(rb.getString("AlternateTrack"));
    	pack();
    	if (getWidth() < 300 || getHeight() < 100)
    		setSize(300, 100);
    	setVisible(true); 	
	}
	
	public void buttonActionPerformed(java.awt.event.ActionEvent ae) {
		if (ae.getSource() == saveButton){
			if (trackBox.getSelectedItem() != null && !trackBox.getSelectedItem().equals(""))
				_track.setAlternativeTrack((Track)trackBox.getSelectedItem());
			else 
				_track.setAlternativeTrack(null);
			OperationsXml.save();
		}		
	}
	
	
	static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TrackEditFrame.class.getName());
}