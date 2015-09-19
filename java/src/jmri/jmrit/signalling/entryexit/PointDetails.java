package jmri.jmrit.signalling.entryexit;

import java.util.Hashtable;
import java.util.Map.Entry;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import jmri.jmrit.display.layoutEditor.LayoutEditor;
import jmri.jmrit.display.layoutEditor.LayoutBlock;
import jmri.jmrit.display.layoutEditor.LevelXing;
import jmri.jmrit.display.layoutEditor.LayoutTurnout;
import jmri.jmrit.display.layoutEditor.LayoutSlip;
import jmri.jmrit.display.layoutEditor.PositionablePoint;
import jmri.jmrit.display.SensorIcon;

import jmri.Sensor;
import jmri.SignalMast;
import jmri.SignalHead;
import jmri.NamedBean;
import jmri.InstanceManager;

import jmri.jmrit.signalling.EntryExitPairs;

public class PointDetails {
    //May want to look at putting a listener on the refLoc to listen to updates to blocks, signals and sensors attached to it
    LayoutEditor panel = null;
    LayoutBlock facing;
    LayoutBlock protecting;
    private NamedBean refObj;
    private Object refLoc;
    private Sensor sensor;
    private SignalMast signalmast;
    private SignalHead signalhead;
    
    static int nxButtonTimeout = 10;
    
    Source sourceRoute;
    transient Hashtable<DestinationPoints, Source> destinations = new Hashtable<DestinationPoints, Source>(5);
    
    public PointDetails(LayoutBlock facing, LayoutBlock protecting){
        this.facing=facing;
        this.protecting = protecting;
    }
    
    LayoutBlock getFacing(){ return facing; }
    LayoutBlock getProtecting(){ return protecting; }
    
    //This might be better off a ref to the source pointdetail.
    boolean routeToSet = false;
    void setRouteTo(boolean boo){
        routeToSet = boo;
    }
    
    boolean routeFromSet = false;
    void setRouteFrom(boolean boo){
        routeFromSet = boo;
    }
    
    public void setPanel(LayoutEditor panel){
        this.panel = panel;
    }
    
    void setSensor(Sensor sen){
        if(sensor==sen)
            return;
        if(sensor!=null)
            sensor.removePropertyChangeListener(nxButtonListener);
        sensor = sen;
        if(sensor!=null)
            sensor.addPropertyChangeListener(nxButtonListener);
    }
    
    void addSensorList(){
        sensor.addPropertyChangeListener(nxButtonListener);
    }
    
    void removeSensorList(){
        sensor.removePropertyChangeListener(nxButtonListener);
    }
    
    //Sensor getSensor() { return sensor; }
    
    protected PropertyChangeListener nxButtonListener = new PropertyChangeListener() {
    //First off if we were inactive, and now active
        public void propertyChange(PropertyChangeEvent e) {
            if(!e.getPropertyName().equals("KnownState"))
                return;
            int now = ((Integer) e.getNewValue()).intValue();
            int old = ((Integer) e.getOldValue()).intValue();
            
            if((old==Sensor.UNKNOWN) || (old==Sensor.INCONSISTENT)){
                setButtonState(EntryExitPairs.NXBUTTONINACTIVE);
                return;
            }
            
            DestinationPoints destPoint = null;
            
            for(Entry<DestinationPoints, Source> dp: destinations.entrySet()){
                destPoint = dp.getKey();
                if(destPoint.isEnabled() && dp.getValue().getPoint().getNXState()==EntryExitPairs.NXBUTTONSELECTED){
                    setButtonState(EntryExitPairs.NXBUTTONSELECTED);
                    destPoint.activeBean(false);
                    return;
                }
            }
            
            if(sourceRoute!=null){
                if(now==Sensor.ACTIVE && getNXState()==EntryExitPairs.NXBUTTONINACTIVE){
                    setButtonState(EntryExitPairs.NXBUTTONSELECTED);
                    for(Entry<PointDetails, DestinationPoints> en : sourceRoute.pointToDest.entrySet()){
                        //Sensor sen = getSensorFromPoint(en.getKey().getPoint());
                        //Set a time out on the source sensor, so that if its state hasn't been changed, then we will clear it out.
                        if(en.getValue().isEnabled() && !en.getValue().getUniDirection()){
                            if(en.getKey().getNXState()==EntryExitPairs.NXBUTTONSELECTED){
                                sourceRoute.activeBean(en.getValue(), true);
                            }
                        }
                    }
                } else if (now==Sensor.INACTIVE && getNXState()==EntryExitPairs.NXBUTTONSELECTED){
                    //sensor inactive, nxbutton state was selected, going to set back to inactive - ie user cancelled button
                    setButtonState(EntryExitPairs.NXBUTTONINACTIVE);
                } else if (now==Sensor.INACTIVE && getNXState()==EntryExitPairs.NXBUTTONACTIVE){
                    //Sensor gone inactive, while nxbutton was selected - potential start of user either clear route or setting another
                    setButtonState(EntryExitPairs.NXBUTTONSELECTED);
                    for(Entry<PointDetails, DestinationPoints> en : sourceRoute.pointToDest.entrySet()){
                        //Sensor sen = getSensorFromPoint(en.getKey().getPoint());
                        //Set a time out on the source sensor, so that if its state hasn't been changed, then we will clear it out.
                        if(en.getValue().isEnabled() && !en.getValue().getUniDirection()){
                            if(en.getKey().getNXState()==EntryExitPairs.NXBUTTONSELECTED){
                                sourceRoute.activeBean(en.getValue(), false);
                            }
                        }
                    }
                }
            } else if (destPoint!=null){
                //Button set as a destination but has no source, it has had a change in state
                if(now==Sensor.ACTIVE){
                    //State now is Active will set flashing
                    setButtonState(EntryExitPairs.NXBUTTONSELECTED);
                } else if(getNXState()==EntryExitPairs.NXBUTTONACTIVE){
                    //Sensor gone inactive while it was previosly active
                    setButtonState(EntryExitPairs.NXBUTTONSELECTED);
                } else if(getNXState()==EntryExitPairs.NXBUTTONSELECTED){
                    //Sensor gone inactive while it was previously selected therefore will cancel
                    setButtonState(EntryExitPairs.NXBUTTONINACTIVE);
                }
            }
        }
    };
    
    void setSignalMast(SignalMast mast) {
        signalmast = mast;
    }
    
    void setSource(Source src){
        if(sourceRoute==src)
            return;
        sourceRoute=src;
    }
    
    void setDestination(DestinationPoints srcdp, Source src){
        if(!destinations.containsKey(srcdp)){
            destinations.put(srcdp, src);
        }
    }
    
    void removeDestination(DestinationPoints srcdp){
        destinations.remove(srcdp);
        if(sourceRoute==null && destinations.size()==0){
            stopFlashSensor();
            sensor.removePropertyChangeListener(nxButtonListener);
            setSensor(null);
        }
    }
    
    void removeSource(Source src){
        sourceRoute=null;
        if(destinations.size()==0) {
            stopFlashSensor();
            sensor.removePropertyChangeListener(nxButtonListener);
            setSensor(null);
        }
    }
    
    private int nxButtonState = EntryExitPairs.NXBUTTONINACTIVE;
    
    void setButtonState(int state){
        setNXButtonState(state);
    }
    
    void setNXState(int state){
        if(state==nxButtonState)
            return;
        if(state==EntryExitPairs.NXBUTTONSELECTED) {
            nxButtonTimeOut();
            flashSensor();
        } else {
            cancelNXButtonTimeOut();
            stopFlashSensor();
        }
        nxButtonState=state;
    }
    
    int getNXState(){
        return nxButtonState;
    }
    
    SignalMast getSignalMast() { return signalmast; }
    
    void setSignalHead(SignalHead head){
        signalhead = head;
    }
    
    SignalHead getSignalHead() { return signalhead; }
    
    public LayoutEditor getPanel() { return panel; }
    
    public void setRefObject(NamedBean refObs){
        refObj = refObs;
        if (panel!=null && refObj!=null){
            if (refObj instanceof SignalMast){
                String mast = ((SignalMast)refObj).getUserName();
                refLoc = panel.findPositionablePointByEastBoundSignalMast(mast);
                if(refLoc==null)
                    refLoc = panel.findPositionablePointByWestBoundSignalMast(mast);
                if(refLoc==null)
                    refLoc = panel.findLayoutTurnoutBySignalMast(mast);
                if(refLoc==null)
                    refLoc = panel.findLevelXingBySignalMast(mast);
                if(refLoc==null)
                    refLoc = panel.findLayoutSlipBySignalMast(mast);
                if(refLoc==null){
                    mast = ((SignalMast)refObj).getSystemName();
                    if(refLoc==null)
                        refLoc = panel.findPositionablePointByWestBoundSignalMast(mast);
                    if(refLoc==null)
                        refLoc = panel.findLayoutTurnoutBySignalMast(mast);
                    if(refLoc==null)
                        refLoc = panel.findLevelXingBySignalMast(mast);
                    if(refLoc==null)
                        refLoc = panel.findLayoutSlipBySignalMast(mast);
                }
            } else if (refObj instanceof Sensor) {
                String sourceSensor = ((Sensor)refObj).getSystemName();
                refLoc = panel.findPositionablePointByEastBoundSensor(sourceSensor);
                if(refLoc==null)
                    refLoc = panel.findPositionablePointByWestBoundSensor(sourceSensor);
                if(refLoc==null)
                    refLoc = panel.findLayoutTurnoutBySensor(sourceSensor);
                if(refLoc==null)
                    refLoc = panel.findLevelXingBySensor(sourceSensor);
                if(refLoc==null)
                    refLoc = panel.findLayoutSlipBySensor(sourceSensor);
                if(refLoc==null){
                    sourceSensor = ((Sensor)refObj).getUserName();
                    refLoc = panel.findPositionablePointByEastBoundSensor(sourceSensor);
                    if(refLoc==null)
                        refLoc = panel.findPositionablePointByWestBoundSensor(sourceSensor);
                    if(refLoc==null)
                        refLoc = panel.findLayoutTurnoutBySensor(sourceSensor);
                    if(refLoc==null)
                        refLoc = panel.findLevelXingBySensor(sourceSensor);
                    if(refLoc==null)
                        refLoc = panel.findLayoutSlipBySensor(sourceSensor);
                }
                setSensor((Sensor)refObj);
            } else if (refObj instanceof SignalHead){
                String signal = ((SignalHead)refObj).getDisplayName();
                refLoc = panel.findPositionablePointByEastBoundSignal(signal);
                if(refLoc==null)
                    refLoc = panel.findPositionablePointByWestBoundSignal(signal);
            }
        }
        if (refLoc!=null){
            if(refLoc instanceof PositionablePoint){
                //((PositionablePoint)refLoc).addPropertyChangeListener(this);
            } else if (refLoc instanceof LayoutTurnout){
                //((LayoutTurnout)refLoc).addPropertyChangeListener(this);
            } else if (refLoc instanceof LevelXing){
                //((LevelXing)refLoc).addPropertyChangeListener(this);
            } else if (refLoc instanceof LayoutSlip){
                //((Layoutslip)refLoc).addPropertyChangeListener(this);
            }
        }
        //With this set ref we can probably add a listener to it, so that we can detect when a change to the point details takes place
    }
    
    public NamedBean getRefObject(){ return refObj; }
    
    public Object getRefLocation() { return refLoc; }
    
    //LayoutEditor getLayoutEditor() { return panel; }
    
    boolean isRouteToPointSet() { return routeToSet; }
    boolean isRouteFromPointSet() { return routeFromSet; }
    
    public String getDisplayName(){
        if(sensor!=null){
            String description = sensor.getDisplayName();
            if(signalmast!=null){
                description = description + " (" + signalmast.getDisplayName() +")";
            }
            return description;
        }
         
        if(refObj instanceof SignalMast){
            return ((SignalMast)refObj).getDisplayName();
        } else if (refObj instanceof Sensor) {
            return ((Sensor)refObj).getDisplayName();
        } else if (refObj instanceof SignalHead){
            return ((SignalHead)refObj).getDisplayName();
        }
        return "no display name";
    }
    
    transient Thread nxButtonTimeOutThr;
    
    void nxButtonTimeOut(){
        if((nxButtonTimeOutThr!=null) && (nxButtonTimeOutThr.isAlive())){
            return;
        }
        extendedtime = true;
        class ButtonTimeOut implements Runnable {
            ButtonTimeOut(){
            }
            public void run() {
                try {
                    //Stage one default timer for the button if no other button has been pressed
                    Thread.sleep(nxButtonTimeout*1000);
                    //Stage two if an extended time out has been requested
                    if(extendedtime){
                        Thread.sleep(60000);  //timeout after a minute waiting for the sml to set.
                    }
                } catch (InterruptedException ex) {
                    log.debug("Flash timer cancelled");
                }
                setNXButtonState(EntryExitPairs.NXBUTTONINACTIVE);
            }
        }
        ButtonTimeOut t = new ButtonTimeOut();
        nxButtonTimeOutThr = new Thread(t, "NX Button Timeout " + getSensor().getDisplayName());
        
        nxButtonTimeOutThr.start();
    }
    
    void cancelNXButtonTimeOut(){
        if(nxButtonTimeOutThr!=null)
            nxButtonTimeOutThr.interrupt();
    
    }
    
    boolean extendedtime = false;
        
    public void flashSensor(){
        for(SensorIcon si : getPanel().sensorList){
            if(si.getSensor()==getSensor()){
                si.flashSensor(2, Sensor.ACTIVE, Sensor.INACTIVE);
            }
        }
    }
    
    public void stopFlashSensor(){
        for(SensorIcon si : getPanel().sensorList){
            if(si.getSensor()==getSensor()){
                si.stopFlash();
            }
        }
    }
    
    synchronized public void setNXButtonState(int state){
        if(getSensor()==null)
            return;
        if(state==EntryExitPairs.NXBUTTONINACTIVE){
            //If a route is set to or from out point then we need to leave/set the sensor to ACTIVE
            if(isRouteToPointSet()){
                state=EntryExitPairs.NXBUTTONACTIVE;
            } else if(isRouteFromPointSet()){
                state=EntryExitPairs.NXBUTTONACTIVE;
            }
        }
        setNXState(state);
        int sensorState = Sensor.UNKNOWN;
        switch(state){
            case EntryExitPairs.NXBUTTONINACTIVE : sensorState = Sensor.INACTIVE;
                                                    break;
            case EntryExitPairs.NXBUTTONACTIVE   : sensorState = Sensor.ACTIVE;
                                                    break;
            case EntryExitPairs.NXBUTTONSELECTED : sensorState = Sensor.ACTIVE;
                                                    break;
            default                             : sensorState = Sensor.UNKNOWN;
                                                    break;
        }
        
        //Might need to clear listeners at the stage and then reapply them after.
        if(getSensor().getKnownState()!=sensorState){
            removeSensorList();
            try {
                getSensor().setKnownState(sensorState);
            } catch (jmri.JmriException ex){
                log.error(ex);
            }
            addSensorList();
        }
    }
    
    Sensor getSensor(){
        if (getRefObject()==null)
            return null;
        if((getPanel()!=null) && (!getPanel().isEditable()) && (sensor!=null))
            return sensor;
        
        if (getRefObject() instanceof Sensor)
            return (Sensor)getRefObject();
        Object objLoc = getRefLocation();
        Object objRef = getRefObject();
        SignalMast mast=null;
        SignalHead head=null;
        String username = "";
        String systemname = "";
        Sensor sensor = null;
        if(objRef instanceof SignalMast){
            mast = (SignalMast)objRef;
            username = mast.getUserName();
            systemname = mast.getSystemName();
        }
        if(objRef instanceof SignalHead){
            head = (SignalHead)objRef;
            username = head.getUserName();
            systemname = head.getSystemName();
        }
        jmri.SensorManager sm = InstanceManager.sensorManagerInstance();
        if (objLoc instanceof PositionablePoint){
            PositionablePoint p = (PositionablePoint)objLoc;
            if(mast!=null) {
                if((p.getEastBoundSignalMast().equals(username)) || 
                        p.getEastBoundSignalMast().equals(systemname))
                    sensor = sm.getSensor(p.getEastBoundSensor());
                else if((p.getWestBoundSignalMast().equals(username)) || 
                        p.getWestBoundSignalMast().equals(systemname))
                    sensor = sm.getSensor(p.getWestBoundSensor());
            }
            else if(head!=null) {
                if((p.getEastBoundSignal().equals(username)) || 
                        p.getEastBoundSignal().equals(systemname))
                    sensor = sm.getSensor(p.getEastBoundSensor());
                else if((p.getWestBoundSignal().equals(username)) || 
                        p.getWestBoundSignal().equals(systemname))
                    sensor = sm.getSensor(p.getWestBoundSensor());
            }
        } else if (objLoc instanceof LayoutTurnout) {
            LayoutTurnout t = (LayoutTurnout)objLoc;
            if(mast!=null){
                if((t.getSignalAMast().equals(username)) || (t.getSignalAMast().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorA());
                else if((t.getSignalBMast().equals(username)) || (t.getSignalBMast().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorB());
                else if((t.getSignalCMast().equals(username)) || (t.getSignalCMast().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorC());
                else if((t.getSignalDMast().equals(username)) || (t.getSignalDMast().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorD());
            }
            if(head!=null){
                if((t.getSignalA1Name().equals(username)) || (t.getSignalA1Name().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorA());
                else if((t.getSignalA2Name().equals(username)) || (t.getSignalA2Name().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorA());
                else if((t.getSignalA3Name().equals(username)) || (t.getSignalA3Name().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorA());
                else if((t.getSignalB1Name().equals(username)) || (t.getSignalB1Name().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorB());
                else if((t.getSignalB2Name().equals(username)) || (t.getSignalB2Name().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorB());
                else if((t.getSignalC1Name().equals(username)) || (t.getSignalC1Name().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorC());
                else if((t.getSignalC2Name().equals(username)) || (t.getSignalC2Name().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorC());
                else if((t.getSignalD1Name().equals(username)) || (t.getSignalD1Name().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorD());
                else if((t.getSignalD2Name().equals(username)) || (t.getSignalD2Name().equals(systemname)))
                    sensor = sm.getSensor(t.getSensorD());
            }
        } else if (objLoc instanceof LevelXing){
            LevelXing x = (LevelXing)objLoc;
            if(mast!=null){
                if((x.getSignalAMastName().equals(username)) || (x.getSignalAMastName().equals(systemname)))
                    sensor = sm.getSensor(x.getSensorAName());
                else if((x.getSignalBMastName().equals(username)) || (x.getSignalBMastName().equals(systemname)))
                    sensor = sm.getSensor(x.getSensorBName());
                else if((x.getSignalCMastName().equals(username)) || (x.getSignalCMastName().equals(systemname)))
                    sensor = sm.getSensor(x.getSensorCName());
                else if((x.getSignalDMastName().equals(username)) || (x.getSignalDMastName().equals(systemname)))
                    sensor = sm.getSensor(x.getSensorDName());
            }
            if(head!=null){
                if((x.getSignalAName().equals(username)) || (x.getSignalAName().equals(systemname)))
                    sensor = sm.getSensor(x.getSensorAName());
                else if((x.getSignalBName().equals(username)) || (x.getSignalBName().equals(systemname)))
                    sensor = sm.getSensor(x.getSensorBName());
                else if((x.getSignalCName().equals(username)) || (x.getSignalCName().equals(systemname)))
                    sensor = sm.getSensor(x.getSensorCName());
                else if((x.getSignalDName().equals(username)) || (x.getSignalDName().equals(systemname)))
                    sensor = sm.getSensor(x.getSensorDName());
            }
        } else if (objLoc instanceof LayoutSlip) {
            LayoutSlip sl = (LayoutSlip)objLoc;
            if(mast!=null){
                if((sl.getSignalAMast().equals(username)) || (sl.getSignalAMast().equals(systemname)))
                    sensor = sm.getSensor(sl.getSensorA());
                else if((sl.getSignalBMast().equals(username)) || (sl.getSignalBMast().equals(systemname)))
                    sensor = sm.getSensor(sl.getSensorB());
                else if((sl.getSignalCMast().equals(username)) || (sl.getSignalCMast().equals(systemname)))
                    sensor = sm.getSensor(sl.getSensorC());
                else if((sl.getSignalDMast().equals(username)) || (sl.getSignalDMast().equals(systemname)))
                    sensor = sm.getSensor(sl.getSensorD());
            }
            if(head!=null){
                if((sl.getSignalA1Name().equals(username)) || (sl.getSignalA1Name().equals(systemname)))
                    sensor = sm.getSensor(sl.getSensorA());
                else if((sl.getSignalB1Name().equals(username)) || (sl.getSignalB1Name().equals(systemname)))
                    sensor = sm.getSensor(sl.getSensorB());
                else if((sl.getSignalC1Name().equals(username)) || (sl.getSignalC1Name().equals(systemname)))
                    sensor = sm.getSensor(sl.getSensorC());
                else if((sl.getSignalD1Name().equals(username)) || (sl.getSignalD1Name().equals(systemname)))
                    sensor = sm.getSensor(sl.getSensorD());
            }
        }
        setSensor(sensor);
        return sensor;
    }
    
    NamedBean getSignal(){
        if((getPanel()!=null) && (!getPanel().isEditable()) && (getSignalMast()!=null))
            return getSignalMast();
        if((getPanel()!=null) && (!getPanel().isEditable()) && (getSignalHead()!=null))
            return getSignalHead();
        jmri.SignalMastManager sm = InstanceManager.signalMastManagerInstance();
        jmri.SignalHeadManager sh = InstanceManager.signalHeadManagerInstance();
        NamedBean signal = null;
        
        if(getRefObject()==null) {
            log.error("Signal not found at point");
            return null;
        } else if (getRefObject() instanceof SignalMast){
            signal =  getRefObject();
            setSignalMast(((SignalMast)getRefObject()));
            return signal;
        } else if (getRefObject() instanceof SignalHead){
            signal =  getRefObject();
            setSignalHead(((SignalHead)getRefObject()));
            return signal;
        }

        
        Sensor sen = (Sensor) getRefObject();
        log.debug("looking at Sensor " + sen.getDisplayName());
        String username = sen.getUserName();
        String systemname = sen.getSystemName();
        if(getRefLocation() instanceof PositionablePoint){
            PositionablePoint p = (PositionablePoint)getRefLocation();
            if((p.getEastBoundSensor().equals(username)) || 
                    p.getEastBoundSensor().equals(systemname)){
                    
                if(!p.getEastBoundSignalMast().equals(""))
                    signal =  sm.getSignalMast(p.getEastBoundSignalMast());
                    
                else if(!p.getEastBoundSignal().equals(""))
                    signal =  sh.getSignalHead(p.getEastBoundSignal());
            }
            else if((p.getWestBoundSensor().equals(username)) || 
                    p.getWestBoundSensor().equals(systemname)){
                    
                if(!p.getWestBoundSignalMast().equals(""))
                    signal =  sm.getSignalMast(p.getWestBoundSignalMast());
                    
                else if(!p.getWestBoundSignal().equals(""))
                    signal =  sh.getSignalHead(p.getWestBoundSignal());
            }
        }
        else if(getRefLocation() instanceof LayoutTurnout){
            LayoutTurnout t = (LayoutTurnout)getRefLocation();
            if(t.getSensorA().equals(username) || t.getSensorA().equals(systemname))
                if(!t.getSignalAMast().equals(""))
                    signal =  sm.getSignalMast(t.getSignalAMast());
                else if(!t.getSignalA1Name().equals(""))
                    signal =  sh.getSignalHead(t.getSignalA1Name());
                    
            else if(t.getSensorB().equals(username) || t.getSensorB().equals(systemname))
                if(!t.getSignalBMast().equals(""))
                    signal =  sm.getSignalMast(t.getSignalBMast());
                else if(!t.getSignalB1Name().equals(""))
                    signal =  sh.getSignalHead(t.getSignalB1Name());
                    
            else if(t.getSensorC().equals(username) || t.getSensorC().equals(systemname))
                if(!t.getSignalCMast().equals(""))
                    signal =  sm.getSignalMast(t.getSignalCMast());
                else if(!t.getSignalC1Name().equals(""))
                    signal =  sh.getSignalHead(t.getSignalC1Name());
                    
            else if(t.getSensorD().equals(username) || t.getSensorD().equals(systemname))
                if(!t.getSignalDMast().equals(""))
                    signal =  sm.getSignalMast(t.getSignalDMast());
                else if(!t.getSignalD1Name().equals(""))
                    signal =  sh.getSignalHead(t.getSignalD1Name());
        }
        
        else if(getRefLocation() instanceof LevelXing){
            LevelXing x = (LevelXing)getRefLocation();
            if(x.getSensorAName().equals(username) || x.getSensorAName().equals(systemname))
                if(!x.getSignalAMastName().equals(""))
                    signal =  sm.getSignalMast(x.getSignalAMastName());
                else if(!x.getSignalAName().equals(""))
                    signal =  sh.getSignalHead(x.getSignalAName());
                    
            else if(x.getSensorBName().equals(username) || x.getSensorBName().equals(systemname))
                if(!x.getSignalBMastName().equals(""))
                    signal =  sm.getSignalMast(x.getSignalBMastName());
                else if(!x.getSignalBName().equals(""))
                    signal =  sh.getSignalHead(x.getSignalBName());
                    
            else if(x.getSensorCName().equals(username) || x.getSensorCName().equals(systemname))
                if(!x.getSignalCMastName().equals(""))
                    signal =  sm.getSignalMast(x.getSignalCMastName());
                else if(!x.getSignalCName().equals(""))
                    signal =  sh.getSignalHead(x.getSignalCName());
                    
            else if(x.getSensorDName().equals(username) || x.getSensorDName().equals(systemname))
                if(!x.getSignalDMastName().equals(""))
                    signal =  sm.getSignalMast(x.getSignalDMastName());
                else if(!x.getSignalDName().equals(""))
                    signal =  sh.getSignalHead(x.getSignalDName());
        }
        else if(getRefLocation() instanceof LayoutSlip){
            LayoutSlip t = (LayoutSlip)getRefLocation();
            if(t.getSensorA().equals(username) || t.getSensorA().equals(systemname))
                if(!t.getSignalAMast().equals(""))
                    signal =  sm.getSignalMast(t.getSignalAMast());
                else if(!t.getSignalA1Name().equals(""))
                    signal =  sh.getSignalHead(t.getSignalA1Name());
                    
            else if(t.getSensorB().equals(username) || t.getSensorB().equals(systemname))
                if(!t.getSignalBMast().equals(""))
                    signal =  sm.getSignalMast(t.getSignalBMast());
                else if(!t.getSignalB1Name().equals(""))
                    signal =  sh.getSignalHead(t.getSignalB1Name());
                    
            else if(t.getSensorC().equals(username) || t.getSensorC().equals(systemname))
                if(!t.getSignalCMast().equals(""))
                    signal =  sm.getSignalMast(t.getSignalCMast());
                else if(!t.getSignalC1Name().equals(""))
                    signal =  sh.getSignalHead(t.getSignalC1Name());
                    
            else if(t.getSensorD().equals(username) || t.getSensorD().equals(systemname))
                if(!t.getSignalDMast().equals(""))
                    signal =  sm.getSignalMast(t.getSignalDMast());
                else if(!t.getSignalD1Name().equals(""))
                    signal =  sh.getSignalHead(t.getSignalD1Name());
        }
        if(signal instanceof SignalMast)
            setSignalMast(((SignalMast)signal));
        else if (signal instanceof SignalHead)
            setSignalHead(((SignalHead)signal));
        return signal;
    }
    @Override
    public boolean equals(Object obj){
        if(obj ==this)
            return true;
        if(obj ==null)
            return false;
            
            if(!(getClass()==obj.getClass()))
                return false;
            else{
                PointDetails tmp = (PointDetails)obj;
                if(tmp.getFacing()!=this.facing)
                    return false;
                if(tmp.getProtecting()!=this.protecting)
                    return false;
                if(tmp.getPanel()!=this.panel)
                    return false;
            }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.panel != null ? this.panel.hashCode() : 0);
        hash = 37 * hash + (this.facing != null ? this.facing.hashCode() : 0);
        hash = 37 * hash + (this.protecting != null ? this.protecting.hashCode() : 0);
        return hash;
    }
    
    java.beans.PropertyChangeSupport pcs = new java.beans.PropertyChangeSupport(this);
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    protected void firePropertyChange(String p, Object old, Object n) { pcs.firePropertyChange(p,old,n);}
    
    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PointDetails.class.getName());
}