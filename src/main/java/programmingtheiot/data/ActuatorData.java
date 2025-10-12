/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * You may find it more helpful to your design to adjust the
 * functionality, constants and interfaces (if there are any)
 * provided within in order to meet the needs of your specific
 * Programming the Internet of Things project.
 */

package programmingtheiot.data;

import java.io.Serializable;

import programmingtheiot.common.ConfigConst;

/**
 * Shell representation of class for student implementation.
 *
 */
public class ActuatorData extends BaseIotData implements Serializable
{
	// NOTE: You should create your own unique serialVersionUID
	private static final long serialVersionUID = 576867023251185906L;
	
	private int     command      = ConfigConst.DEFAULT_COMMAND;
	private float   value        = ConfigConst.DEFAULT_VAL;
	private boolean isResponse   = false;
	private String  stateData    = "";
    
	// constructors
	
	public ActuatorData()
	{
		super();
	}
	
	
	// public methods
	
	public int getCommand()
	{
		return this.command;
	}
	
	public String getStateData()
	{
		return this.stateData;
	}
	
	public float getValue()
	{
		return this.value;
	}
	
	public boolean isResponseFlagEnabled()
	{
		return this.isResponse;
	}
	
	public void setAsResponse()
	{
		updateTimeStamp();
		this.isResponse = true;
	}
	
	public void setCommand(int command)
	{
		updateTimeStamp();
		this.command = command;
	}
	
	public void setStateData(String stateData)
	{
		updateTimeStamp();
		
		if (stateData != null) {
			this.stateData = stateData;
		}
	}
	
	public void setValue(float val)
	{
		updateTimeStamp();
		this.value = val;
	}
	
	
	// protected methods
	
	protected void handleUpdateData(BaseIotData data)
	{
		if (data instanceof ActuatorData) {
			ActuatorData aData = (ActuatorData) data;
			this.setCommand(aData.getCommand());
			this.setValue(aData.getValue());
			this.setStateData(aData.getStateData());
			
			if (aData.isResponseFlagEnabled()) {
				this.isResponse = true;
			}
		}
	}
}
