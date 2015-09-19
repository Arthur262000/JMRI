package jmri.jmrit.vsdecoder.swing;
/*
 * <hr>
 * This file is part of JMRI.
 * <P>
 * JMRI is free software; you can redistribute it and/or modify it under 
 * the terms of version 2 of the GNU General Public License as published 
 * by the Free Software Foundation. See the "COPYING" file for a copy
 * of this license.
 * <P>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 * <P>
 *
 * @author			Mark Underwood Copyright (C) 2011
 * @version			$Revision: 21510 $
 */

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.util.HashMap;
import java.util.List;
import jmri.Reporter;
import jmri.ReporterManager;
import jmri.PhysicalLocationReporter;
import jmri.util.PhysicalLocation;
import jmri.jmrit.vsdecoder.VSDecoderManager;
import jmri.jmrit.vsdecoder.swing.ManageLocationsFrame;
import jmri.jmrit.vsdecoder.listener.ListeningSpot;
import jmri.jmrit.operations.locations.LocationManager;
import jmri.jmrit.operations.locations.Location;


public class ManageLocationsAction extends AbstractAction {

    private ManageLocationsFrame f = null;
    private HashMap<String, PhysicalLocation> reporterMap;
    private HashMap<String, PhysicalLocation> opsMap;
    private ListeningSpot listenerLoc;

    public ManageLocationsAction(String s, String a) {
	super(s);
    }

    public void actionPerformed(ActionEvent e) {
	if (f == null || !f.isVisible()) {
	    listenerLoc = VSDecoderManager.instance().getVSDecoderPreferences().getListenerPosition();
	    ReporterManager rmgr = jmri.InstanceManager.reporterManagerInstance();
	    String[] nameArray = rmgr.getSystemNameArray();
	    Object[][] reporterTable = new Object[nameArray.length][5];
	    reporterMap = new HashMap<String, PhysicalLocation>();
	    int i = 0;
	    for (String s : nameArray) {
		Reporter r = rmgr.getByDisplayName(s);
		if (r instanceof PhysicalLocationReporter) {
		    reporterMap.put(s, ((PhysicalLocationReporter)r).getPhysicalLocation());
		    PhysicalLocation p = ((PhysicalLocationReporter)r).getPhysicalLocation();
		    reporterTable[i][0] = s;
		    reporterTable[i][1] = new Boolean(true);
		    reporterTable[i][2] = p.getX();
		    reporterTable[i][3] = p.getY();
		    reporterTable[i][4] = p.getZ();
		} else {
		    reporterTable[i][0] = s;
		    reporterTable[i][1] = new Boolean(false);
		    reporterTable[i][2] = new Float(0.0f);
		    reporterTable[i][3] = new Float(0.0f);
		    reporterTable[i][4] = new Float(0.0f);
		}
		i++;
	    }
	    LocationManager lmgr = LocationManager.instance();
	    List<String> lnames = lmgr.getLocationsByIdList();
	    opsMap = new HashMap<String, PhysicalLocation>();
	    log.debug("TableSize : " + lnames.size());
	    Object[][] opsTable = new Object[lnames.size()][5];
	    i = 0;
	    for (String s : lnames) {
		Location l = lmgr.getLocationById(s);
		log.debug("i = " + i + "MLA " + s + " Name: " + l.getName() + " table " + opsTable[i]);
		PhysicalLocation p = l.getPhysicalLocation();
		Boolean use = new Boolean(false);
		if (p == PhysicalLocation.Origin) {
		    use = false;
		} else {
		    use = true;
		}
		opsTable[i][0] = l.getName();
		opsTable[i][1] = new Boolean(use);
		opsTable[i][2] = p.getX();
		opsTable[i][3] = p.getY();
		opsTable[i][4] = p.getZ();
		opsMap.put(l.getName(), l.getPhysicalLocation());
		i++;
	    }
	    

	    f = new ManageLocationsFrame(listenerLoc, reporterTable, opsTable);
	}
	f.setExtendedState(Frame.NORMAL);
    }

    static private org.apache.log4j.Logger log = org.apache.log4j.Logger
	.getLogger(ManageLocationsAction.class.getName());

}