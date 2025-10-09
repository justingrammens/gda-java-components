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
public class SensorData extends BaseIotData implements Serializable
{
	// NOTE: You should create your own unique serialVersionUID
	private static final long serialVersionUID = 6283569230384638954L;
	
	// private var's
	private float value = ConfigConst.DEFAULT_VAL;
    
	// constructors
	
	public SensorData()
	{
		super();
	}
	
	
	// public methods
	
	public float getValue()
	{
		return this.value;
	}
	
	public void setValue(float val)
	{
		super.updateTimeStamp();
		this.value = val;
	}
	
	// protected methods
	
	protected void handleUpdateData(BaseIotData data)
	{
		if (data instanceof SensorData) {
			SensorData sData = (SensorData) data;
			this.setValue(sData.getValue());
		}
	}
}